import org.example.checkout.*;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CheckoutServiceTest {

    private final CouponService couponSvc = new CouponService();
    private final ShippingService shipSvc = new ShippingService();
    private final CheckoutService service = new CheckoutService(couponSvc, shipSvc);

    @Test
    public void deveCalcularBasicoSemDescontosEImpostoApenasNaoBook() {
        var itens = List.of(
                new Item("BOOK", 100.00, 1),
                new Item("ELETRONICO", 50.00, 2)
        );

        var res = service.checkout(
                itens, CustomerTier.BASIC, false, "SUL", 3.0,
                null, LocalDate.now(), null
        );

        assertEquals(200.00, res.subtotal);
        assertEquals(0.00, res.discountValue);
        assertEquals(12.00, res.tax);
        assertEquals(35.00, res.shipping);
        assertEquals(247.00, res.total);
    }

    @Test
    public void deveAplicarDescontoSilverComPrimeiraCompra() {
        var itens = List.of(new Item("ROUPA", 60.00, 1));

        var res = service.checkout(
                itens, CustomerTier.SILVER, true, "SUL", 2.0,
                null, LocalDate.now(), null
        );

        // SILVER 5% + PRIMEIRA COMPRA 5%
        assertEquals(60.00, res.subtotal);
        assertEquals(6.00, res.discountValue);
        assertEquals(6.48, res.tax);
        assertEquals(35.00, res.shipping);
        assertEquals(95.48, res.total);
    }

    @Test
    public void deveAplicarDescontosComTetoDeTrintaPorCento() {
        var itens = List.of(new Item("ELETRONICO", 200.00, 1));

        var res = service.checkout(
                itens, CustomerTier.GOLD, true, "SUL", 2.0,
                "DESC20", LocalDate.now(), null
        );

        // GOLD 10% + CUPOM 20% + PRIMEIRA 5% = 35% → limitado a 30%
        assertEquals(200.00, res.subtotal);
        assertEquals(60.00, res.discountValue);
        assertEquals(16.80, res.tax);
        assertEquals(35.00, res.shipping);
        assertEquals(191.80, res.total);
    }

    @Test
    public void deveIgnorarCupomExpiradoOuInvalido() {
        var resExpirado = service.checkout(
                List.of(new Item("ELETRONICO", 100.00, 1)),
                CustomerTier.BASIC, false, "SUL", 3.0,
                "DESC20", LocalDate.now().plusDays(10), null
        );

        var resInvalido = service.checkout(
                List.of(new Item("ELETRONICO", 100.00, 1)),
                CustomerTier.BASIC, false, "SUL", 3.0,
                "INVALIDO", LocalDate.now(), null
        );

        assertEquals(0.0, resExpirado.discountValue);
        assertEquals(0.0, resInvalido.discountValue);
    }

    @Test
    public void deveAplicarFreteGratisComCupomFreteGratis() {
        var res = service.checkout(
                List.of(new Item("ELETRONICO", 100.00, 1)),
                CustomerTier.BASIC, false, "SUL", 4.0,
                "FRETEGRATIS", LocalDate.now(), null
        );

        assertEquals(0.0, res.shipping);
        assertEquals(12.0, res.tax);
        assertEquals(112.0, res.total);
    }

    @Test
    public void deveCobrarFreteQuandoPesoMaiorQueCincoMesmoComFreteGratis() {
        var res = service.checkout(
                List.of(new Item("ELETRONICO", 100.00, 1)),
                CustomerTier.BASIC, false, "SUL", 10.0,
                "FRETEGRATIS", LocalDate.now(), null
        );

        assertEquals(35.0, res.shipping);
    }

    @Test
    public void deveIsentarBookDeImposto() {
        var res = service.checkout(
                List.of(new Item("BOOK", 100.00, 1)),
                CustomerTier.BASIC, false, "SUL", 3.0,
                null, LocalDate.now(), null
        );

        assertEquals(0.0, res.tax);
    }

    @Test
    public void naoDeveAplicarBonusDePrimeiraCompraComSubtotalMenorQue50() {
        var res = service.checkout(
                List.of(new Item("ELETRONICO", 40.00, 1)),
                CustomerTier.SILVER, true, "SUL", 2.0,
                null, LocalDate.now(), null
        );

        // apenas SILVER (5%)
        assertEquals(2.0, res.discountValue);
    }

    @Test
    public void deveLancarErroQuandoPrecoOuQuantidadeInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Item("ELETRONICO", -10, 1));
        assertThrows(IllegalArgumentException.class, () -> new Item("ELETRONICO", 10, 0));
    }

    @Test
    public void deveCalcularFreteSulSudestePorFaixaDePeso() {
        var ship = new ShippingService();

        // Região SUL
        assertEquals(20.0, ship.calculate("SUL", 1.0, 100.0, false));   // até 2kg
        assertEquals(35.0, ship.calculate("SUL", 4.0, 100.0, false));   // até 5kg
        assertEquals(50.0, ship.calculate("SUL", 10.0, 100.0, false));  // acima de 5kg

        // Região SUDESTE
        assertEquals(20.0, ship.calculate("SUDESTE", 1.0, 100.0, false));
        assertEquals(35.0, ship.calculate("SUDESTE", 4.0, 100.0, false));
        assertEquals(50.0, ship.calculate("SUDESTE", 10.0, 100.0, false));
    }

    @Test
    public void deveCalcularFreteNorteNordestePorFaixaDePeso() {
        var ship = new ShippingService();

        // Região NORTE
        assertEquals(30.0, ship.calculate("NORTE", 1.0, 100.0, false));
        assertEquals(55.0, ship.calculate("NORTE", 4.0, 100.0, false));
        assertEquals(80.0, ship.calculate("NORTE", 10.0, 100.0, false));

        // Região NORDESTE
        assertEquals(40.0, ship.calculate("NORDESTE", 3.0, 100.0, false));
        assertEquals(40.0, ship.calculate("NORDESTE", 10.0, 100.0, false));
    }

    @Test
    public void deveCalcularFreteOutrasRegioesComTarifaFixa() {
        var ship = new ShippingService();
        assertEquals(40.0, ship.calculate("CENTRO-OESTE", 10.0, 100.0, false));
    }

    @Test
    public void deveRetornarZeroQuandoFreteGratisEhTrue() {
        var ship = new ShippingService();
        assertEquals(0.0, ship.calculate("SUL", 3.0, 100.0, false));
    }

    @Test
    public void deveLancarErroQuandoPesoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> shipSvc.calculate("SUL", -5, 100.0, false));
    }

    @Test
    public void deveCobrirTodosOsCuponsDoService() {
        var svc = new CouponService();

        // DESC10: sempre válido
        var desc10 = svc.evaluate("DESC10", LocalDate.now(), null, 100.0);
        assertEquals(0.10, desc10.discountPercent, 0.0001);
        assertFalse(desc10.freeShipping);

        // DESC20: válido se subtotal >= 100 e não expirado
        var desc20 = svc.evaluate("DESC20", LocalDate.now(), LocalDate.now().plusDays(1), 200.0);
        assertEquals(0.20, desc20.discountPercent, 0.0001);
        assertFalse(desc20.freeShipping);

        // FRETEGRATIS: sem desconto, mas com frete grátis
        var freteGratis = svc.evaluate("FRETEGRATIS", LocalDate.now(), null, 50.0);
        assertEquals(0.0, freteGratis.discountPercent, 0.0001);
        assertTrue(freteGratis.freeShipping);
    }

}
