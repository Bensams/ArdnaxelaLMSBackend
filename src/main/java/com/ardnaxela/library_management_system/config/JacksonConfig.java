package com.ardnaxela.library_management_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
   import com.fasterxml.jackson.databind.SerializationFeature;
   import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;

   @Configuration
   public class JacksonConfig {

       @Bean
       public ObjectMapper objectMapper() {
           ObjectMapper mapper = new ObjectMapper();
           mapper.registerModule(new JavaTimeModule()); // Handle Java 8 Date/Time types
           mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

           // Set global date format for all LocalDateTime fields
           mapper.setDateFormat(new java.text.SimpleDateFormat("MM/dd/yyyy"));
           return mapper;
       }
   }