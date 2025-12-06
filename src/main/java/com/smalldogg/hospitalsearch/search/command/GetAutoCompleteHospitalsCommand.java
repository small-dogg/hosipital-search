package com.smalldogg.hospitalsearch.search.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAutoCompleteHospitalsCommand {
    private String keyword;
}
