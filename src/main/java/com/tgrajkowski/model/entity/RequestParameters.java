package com.tgrajkowski.model.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float latitude;
    private Float longitude;
    private LocalDateTime date;

    public RequestParameters(Float latitude, Float longitude, LocalDateTime date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
}
