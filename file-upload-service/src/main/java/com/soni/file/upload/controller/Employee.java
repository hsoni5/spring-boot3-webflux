package com.soni.file.upload.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Employee {
    private int age;
    private String name;

    private double salary;
    public static void main(String[] args) {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee(30, "Rupesh", 100));
        employeeList.add(new Employee(35, "Rupesh", 100));
        employeeList.add(new Employee(40, "Rupesh", 100));
        employeeList.add(new Employee(5, "Hari", 100));
         employeeList.stream().filter(employee -> employee.getAge() >=30).forEach(employee ->  {
             employee.setSalary(employee.getSalary() + employee.getSalary() * 10/100);
             System.out.println(employee);
         });


    }

}
