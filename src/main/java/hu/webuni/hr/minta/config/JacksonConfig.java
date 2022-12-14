//package hu.webuni.hr.minta.config;
//
//import java.time.format.DateTimeFormatter;
//
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//
//@Configuration
//public class JacksonConfig {
//
//	
//	private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
//	private static final String dateFormat = "yyyy-MM-dd";
//
//	
//	@Bean
//	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//		return builder -> {
//			builder.simpleDateFormat(dateTimeFormat); //--> java.util.Date, Calendar
//			builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
//			builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
//			builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
//			builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));
//		};
//	}
//}
