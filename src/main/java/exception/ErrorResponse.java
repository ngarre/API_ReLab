package exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    public int code;
    public String title;
    public String message;

}

// Creamos la estructura anterior que cuenta con un código, un título y un mensaje para poder representar todos nuestros errores.