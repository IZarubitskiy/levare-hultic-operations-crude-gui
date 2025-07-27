package com.levare.hultic.ops.common;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExcelTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelTemplateService.class);

    /**
     * Директория с .xlsx-шаблонами (папка “templates” в рабочем каталоге)
     */
    private final Path templatesDir;

    public ExcelTemplateService() {
        this.templatesDir = Paths.get(System.getProperty("user.dir"), "templates");
        if (!Files.exists(templatesDir) || !Files.isDirectory(templatesDir)) {
            throw new IllegalStateException("Не найдена папка шаблонов: " + templatesDir.toAbsolutePath());
        }
        logger.info("Используем директорию шаблонов: {}", templatesDir.toAbsolutePath());
    }

    /**
     * Возвращает список имён файлов-шаблонов (*.xlsx) из папки templates
     */
    public List<String> listTemplates() {
        List<String> names = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(templatesDir, "*.xlsx")) {
            for (Path p : ds) {
                names.add(p.getFileName().toString());
            }
        } catch (IOException e) {
            logger.error("Ошибка при получении списка шаблонов", e);
        }
        return names;
    }

    /**
     * Заполняет выбранный шаблон данными и сохраняет результат в outputFile.
     *
     * @param templateName имя файла-шаблона (например, "OrderForm.xlsx")
     * @param data         Map<имя_диапазона_или_плейсхолдера, значение>
     * @param outputFile   путь для сохранения результата (например, Paths.get("out/Order_123.xlsx"))
     * @throws IOException при ошибках чтения/записи файлов
     */
    public Path generate(String templateName, Map<String, Object> data, Path outputFile) throws IOException {
        Path tpl = templatesDir.resolve(templateName);
        try (InputStream in = Files.newInputStream(tpl);
             Workbook wb = new XSSFWorkbook(in)) {

            //fillByNamedRanges(wb, data);
            fillByPlaceholders(wb, data);

            Files.createDirectories(outputFile.getParent());
            try (OutputStream out = Files.newOutputStream(outputFile)) {
                wb.write(out);
            }

            logger.info("Сформирован файл: {}", outputFile.toAbsolutePath());
            return outputFile;
        }
    }

    /**
     * Заполнение ячеек по именованным диапазонам (Named ranges)
     */
    private void fillByNamedRanges(Workbook wb, Map<String, Object> data) {
        for (Name name : wb.getAllNames()) {
            String key = name.getNameName();
            if (!data.containsKey(key)) continue;

            String ref = name.getRefersToFormula();     // вида 'Sheet1'!$B$3
            String[] parts = ref.split("!");
            String sheetName = parts[0].replace("'", "");
            String cellRef = parts[1].replace("$", "");

            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) continue;

            CellAddress addr = new CellAddress(cellRef);
            Row row = sheet.getRow(addr.getRow());
            Cell cell = row != null ? row.getCell(addr.getColumn()) : null;
            if (cell == null) {
                row = sheet.createRow(addr.getRow());
                cell = row.createCell(addr.getColumn());
            }
            setCellValue(cell, data.get(key));
        }
    }

    /**
     * Заполнение плейсхолдеров вида ${key} в текстовых ячейках
     */
    @SuppressWarnings("unused")
    private void fillByPlaceholders(Workbook wb, Map<String, Object> data) {
        for (Sheet sheet : wb) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING) {
                        String text = cell.getStringCellValue();
                        String replaced = text;
                        for (Map.Entry<String, Object> e : data.entrySet()) {
                            replaced = replaced.replace("${" + e.getKey() + "}", e.getValue().toString());
                        }
                        if (!replaced.equals(text)) {
                            cell.setCellValue(replaced);
                        }
                    }
                }
            }
        }
    }

    /**
     * Устанавливает значение в ячейку, подбирая тип
     */
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            Workbook wb = cell.getSheet().getWorkbook();
            CreationHelper ch = wb.getCreationHelper();
            CellStyle style = wb.createCellStyle();
            style.setDataFormat(ch.createDataFormat().getFormat("dd.MM.yyyy"));
            cell.setCellStyle(style);
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
