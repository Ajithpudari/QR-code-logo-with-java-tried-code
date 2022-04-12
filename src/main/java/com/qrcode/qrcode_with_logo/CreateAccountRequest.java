package com.qrcode.qrcode_with_logo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.servlet.FilterConfig;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateAccountRequest {
    String name;
    String mobile;
    String email;
    String password;


}