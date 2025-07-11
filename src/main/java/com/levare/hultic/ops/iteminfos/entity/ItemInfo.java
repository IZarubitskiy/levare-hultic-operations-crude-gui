package com.levare.hultic.ops.iteminfos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalog item definition for use in GUI context.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfo {

    private Long id;

    private String partNumber;
    private String description;
    private ItemType itemType;

    private List<ItemInfo> analogList = new ArrayList<>();

    private Integer Series;

    private String comments;
}
