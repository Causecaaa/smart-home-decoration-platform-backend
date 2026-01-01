package org.homedecoration.utils;

import org.homedecoration.entity.HouseLayout;
import org.homedecoration.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LayoutPermissionUtil {

    public boolean canEdit(User operator, HouseLayout layout, Long userId) {
        return switch (operator.getRole()) {
            case USER -> layout.getLayoutStatus() == HouseLayout.LayoutStatus.DRAFT
                    && layout.getHouse().getUser().getId().equals(userId);
            case DESIGNER -> layout.getLayoutStatus() == HouseLayout.LayoutStatus.SUBMITTED;
            default -> false;
        };
    }
}

