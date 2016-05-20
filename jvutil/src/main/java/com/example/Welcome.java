package com.example;

public class Welcome {
  public String sayHello(String name) {
    return "hello "+name;
  }

  public static void main(String[] args) {
    System.out.println(new Welcome().sayHello("hhhhh"));
  }
}
