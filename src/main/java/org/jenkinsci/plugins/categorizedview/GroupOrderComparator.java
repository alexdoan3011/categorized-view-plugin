package org.jenkinsci.plugins.categorizedview;

import hudson.model.TopLevelItem;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comparator that sorts TopLevelItems based on their group order in the configuration.
 * Group items are sorted by their position in the grouping rules, while non-group items
 * are sorted alphabetically.
 */
public class GroupOrderComparator implements Comparator<TopLevelItem>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> groupOrderMap = new HashMap<>();
    private final Comparator<TopLevelItem> fallbackComparator = new TopLevelItemComparator();

    /**
     * Constructs a comparator with the given group order mapping.
     *
     * @param groupNames The list of group names in their configured order
     */
    public GroupOrderComparator(final List<String> groupNames) {
        int order = 0;
        for (String groupName : groupNames) {
            groupOrderMap.put(groupName, order++);
        }
    }

    @Override
    public int compare(final TopLevelItem o1, final TopLevelItem o2) {
        // If both are group items, compare by their order
        if (o1 instanceof GroupTopLevelItem && o2 instanceof GroupTopLevelItem) {
            Integer order1 = groupOrderMap.get(o1.getName());
            Integer order2 = groupOrderMap.get(o2.getName());

            // If both groups have defined orders, use them
            if (order1 != null && order2 != null) {
                return order1.compareTo(order2);
            }
            // If only one has a defined order, it comes first
            else if (order1 != null) {
                return -1;
            }
            else if (order2 != null) {
                return 1;
            }
        }
        // If only one is a group item, the group comes first
        else if (o1 instanceof GroupTopLevelItem) {
            return -1;
        }
        else if (o2 instanceof GroupTopLevelItem) {
            return 1;
        }

        // Fall back to alphabetical sorting for non-group items
        return fallbackComparator.compare(o1, o2);
    }
}
