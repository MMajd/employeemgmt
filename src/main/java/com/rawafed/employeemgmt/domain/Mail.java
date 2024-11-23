package com.rawafed.employeemgmt.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Mail {
    String from;
    String to;
    String subject;
    String body;
}
