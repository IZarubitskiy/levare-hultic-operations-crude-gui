package com.levare.hultic.ops.items.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumber {
    private long id;
    private String serialNumber;
    private String partNumber;
}