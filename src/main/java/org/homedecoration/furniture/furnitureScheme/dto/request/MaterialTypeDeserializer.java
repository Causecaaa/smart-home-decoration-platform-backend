// 创建新文件：MaterialTypeDeserializer.java
package org.homedecoration.furniture.furnitureScheme.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;

import java.io.IOException;

public class MaterialTypeDeserializer extends JsonDeserializer<Enum<?>> {

    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String value = p.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        JavaType type = ctxt.getContextualType();
        if (type != null) {
            Class<?> rawClass = type.getRawClass();

            if (rawClass == SchemeRoomMaterial.FloorMaterialType.class) {
                return SchemeRoomMaterial.FloorMaterialType.fromDisplayName(value);
            } else if (rawClass == SchemeRoomMaterial.WallMaterialType.class) {
                return SchemeRoomMaterial.WallMaterialType.fromDisplayName(value);
            } else if (rawClass == SchemeRoomMaterial.CeilingMaterialType.class) {
                return SchemeRoomMaterial.CeilingMaterialType.fromDisplayName(value);
            } else if (rawClass == SchemeRoomMaterial.CabinetMaterialType.class) {
                return SchemeRoomMaterial.CabinetMaterialType.fromDisplayName(value);
            }
        }

        return null;
    }
}
