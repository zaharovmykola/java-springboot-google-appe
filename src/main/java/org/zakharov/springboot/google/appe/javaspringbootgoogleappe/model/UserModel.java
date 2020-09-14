package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "Users")
public class UserModel {
    @Id
    @Expose(serialize = false, deserialize = false)
    private Long id;
    @Index
    @Expose (serialize = false, deserialize = false)
    private String googleId;
    @Index
    @Expose
    private String name;
    @Expose
    private String email;
    @Expose
    private String pictureUrl;
    @Index
    @Expose (serialize = false, deserialize = false)
    private Long userTypeId;
}
