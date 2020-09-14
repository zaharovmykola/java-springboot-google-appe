package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model;

import com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "UserTypes")
public class UserTypeModel {
    @Id
    @Expose
    public Long id;
    @Index
    @Expose
    public String name;
}
