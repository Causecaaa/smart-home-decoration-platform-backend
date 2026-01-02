package org.homedecoration.utils;

import org.homedecoration.entity.HouseLayout;
import org.homedecoration.entity.User;
import org.homedecoration.exception.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class LayoutPermissionUtil {

    public void checkCanEdit(User operator, HouseLayout layout, Long userId) {

        switch (operator.getRole()) {
            case USER -> {
                if (layout.getLayoutStatus() != HouseLayout.LayoutStatus.DRAFT) {
                    throw new AccessDeniedException("Layout is not in DRAFT status");
                }
                if (!layout.getHouse().getUser().getId().equals(userId)) {
                    throw new AccessDeniedException("You are not the owner of this layout");
                }
            }
            case DESIGNER -> {
                if (layout.getLayoutStatus() != HouseLayout.LayoutStatus.SUBMITTED) {
                    throw new AccessDeniedException("Layout is not in SUBMITTED status");
                }
            }
            default -> throw new AccessDeniedException("Unknown role");
        }
    }
}


