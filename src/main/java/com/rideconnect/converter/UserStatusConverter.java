package com.rideconnect.converter;

import com.rideconnect.entity.User;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<User.UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(User.UserStatus attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public User.UserStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return User.UserStatus.fromValue(dbData.trim());
        } catch (IllegalArgumentException e) {
            // Log error và return default value
            System.err.println("Unknown UserStatus value: " + dbData);
            return User.UserStatus.ACTIVE; // hoặc throw exception
        }
    }
}
