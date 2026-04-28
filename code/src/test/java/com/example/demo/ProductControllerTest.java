package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductControllerTest {

  private final ProductController controller = new ProductController();

  @Test
  void getProductsReturnsExpectedCatalog() {
    List<Map<String, Object>> products = controller.getProducts();

    assertAll(
        () -> assertEquals(2, products.size()),
        () -> assertProduct(products.get(0), 1, "Product A", 10.0),
        () -> assertProduct(products.get(1), 2, "Product B", 20.0));
  }

  private void assertProduct(
      Map<String, Object> product, int expectedId, String expectedName, double expectedPrice) {
    assertAll(
        () -> assertEquals(expectedId, product.get("id")),
        () -> assertEquals(expectedName, product.get("name")),
        () -> assertEquals(expectedPrice, ((Number) product.get("price")).doubleValue()));
  }
}
