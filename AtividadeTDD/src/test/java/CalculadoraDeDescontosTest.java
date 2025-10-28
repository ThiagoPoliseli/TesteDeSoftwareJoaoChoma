import org.example.CalculadoraDeDescontos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculadoraDeDescontosTest {

    CalculadoraDeDescontos calc = new CalculadoraDeDescontos();

    @Test
    public void deveRetornarZeroDescontoParaComprasAbaixoDe100() {
        double resultado = calc.calcular(80.0);
        assertEquals(0.0, resultado, 0.001);
    }

    @Test
    public void deveAplicar5PorCentoDeDescontoParaComprasEntre100e500() {
        double resultado = calc.calcular(200.0);
        assertEquals(10.0, resultado, 0.001);
    }

    @Test
    public void deveAplicar10PorCentoDeDescontoParaComprasAcimaDe500() {
        double resultado = calc.calcular(1000.0);
        assertEquals(100.0, resultado, 0.001);
    }

    @Test
    public void deveLancarExcecaoParaValorNegativo() {
        assertThrows(IllegalArgumentException.class, () -> calc.calcular(-50.0));
    }
}
