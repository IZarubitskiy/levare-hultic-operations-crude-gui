package com.example.levarehulticops.mapper;

import org.springframework.stereotype.Component;
import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.dto.JobOrderCreateRequest;
import com.example.levarehulticops.dto.JobOrderUpdateRequest;
import com.example.levarehulticops.dto.JobOrderStatusChangeDto;
import com.example.levarehulticops.dto.JobOrderReadDto;
import com.example.levarehulticops.dto.ItemReadDto;
import com.example.levarehulticops.dto.EmployeeDto;
import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.Employee;
import java.util.stream.Collectors;

/**
 * Manual mapper for JobOrder ↔ DTOs.
 */
@Component
public class JobOrderMapper {

    private final ItemMapper itemMapper;
    private final EmployeeMapper employeeMapper;

    public JobOrderMapper(ItemMapper itemMapper,
                          EmployeeMapper employeeMapper) {
        this.itemMapper = itemMapper;
        this.employeeMapper = employeeMapper;
    }

    /** Entity → Read DTO */
    public JobOrderReadDto toReadDto(JobOrder jo) {
        ItemReadDto itemDto = itemMapper.toReadDto(jo.getItem());
        EmployeeDto respDto = employeeMapper.toDto(jo.getResponsibleEmployee());

        return new JobOrderReadDto(
                jo.getId(),
                jo.getWorkOrder().getId(),
                itemDto,
                jo.getStatus(),
                respDto,
                jo.getComments(),
                jo.getVersion()
        );
    }

    /** Create DTO → new Entity */
    public JobOrder toEntity(JobOrderCreateRequest dto) {
        JobOrder jo = new JobOrder();
        // relationships (workOrder, item, responsibleEmployee) set in service
        jo.setComments(dto.comments());
        // initial status set in service or defaults to CREATED
        return jo;
    }

    /** Update DTO → existing Entity */
    public void updateEntityFromDto(JobOrderUpdateRequest dto, JobOrder jo) {
        // allowed: responsibleEmployee and comments (others in service)
        if (dto.responsibleEmployeeId() != null) {
            Employee e = new Employee();
            e.setId(dto.responsibleEmployeeId());
            jo.setResponsibleEmployee(e);
        }
        if (dto.comments() != null) {
            jo.setComments(dto.comments());
        }
    }

    /** Status-change DTO → existing Entity */
    public void updateStatusFromDto(JobOrderStatusChangeDto dto, JobOrder jo) {
        jo.setStatus(dto.status());
        // version check and save handled in service
    }
}