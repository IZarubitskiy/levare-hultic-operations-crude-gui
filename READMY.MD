Фичи для классов:

1) WorkOrder

    Общий вид:
    - Вывод списка активных - есть
    - Отображение выбранного WorkOrder - есть
    - Удаление выбранного WorkOrder :
      - Функция удаления - есть
      - Невозможно удалить если есть JobOrder - есть
    - Изменение WorkOrder:
      - Функция изменение
      - Изменение статуса оборудования 
      - Изменение оброрудования даже у которого есть JobOrder
    - Функция закрытия
    - Изменение статуса
   Взаимодействия и сценарии:
    - Удаление Workorder:
      Если есть привязанные JobOrder:
      - Необходимо иметь возможность отвязывать их от Workorder и оставлять в работе либо отменять
      - Если JobOrder нет то необходимо менять их статус обратно
2) JobOrder
 
   Общий вид: 
    - Вывод списка активных - есть
    - Отображение выбранного WorkOrder - есть
    - Удаление выбранного:
      - Функция уданиения - есть
      - Изменение статуса оборудования
3) Item
4) Tracking
   - Создать сущность транзакций - есть

--------
Job Order - адо понять как убивать серийные номера.
Допустим ситуацию - мы берем серийник - разбираем его на запчасти и серийный номер его пропадает и появляется новый.
Но этодолжно как то регулироваться.