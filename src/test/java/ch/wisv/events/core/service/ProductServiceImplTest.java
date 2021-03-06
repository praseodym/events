package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.exception.runtime.ProductAlreadyLinkedException;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.ProductServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ProductServiceImplTest extends ServiceTest {

    /**
     * Mock of ProductRepository
     */
    @Mock
    private ProductRepository repository;

    /**
     * Mock of EventService
     */
    @Mock
    private EventService eventService;

    /**
     * ProductService
     */
    private ProductService service;

    /**
     * Default Product
     */
    private Product product;

    /**
     * Method setUp
     */
    @Before
    public void setUp() throws Exception {
        this.service = new ProductServiceImpl(repository);
        this.product = new Product(
                "Product",
                "Description",
                1.d,
                100,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1)
        );
        this.product.setMaxSoldPerCustomer(1);
    }

    /**
     * Method tearDown
     */
    @After
    public void tearDown() throws Exception {
        this.product = null;
    }

    /**
     * Test get all product method
     *
     * @throws Exception when
     */
    @Test
    public void testGetAllProducts() throws Exception {
        when(repository.findAll()).thenReturn(Collections.singletonList(this.product));

        assertEquals(Collections.singletonList(this.product), service.getAllProducts());
    }

    /**
     * Test get all product method
     *
     * @throws Exception when
     */
    @Test
    public void testGetAllProductsEmpty() throws Exception {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), service.getAllProducts());
    }

    /**
     * Method testGetAvailableProducts ...
     *
     * @throws Exception when
     */
    @Test
    public void testGetAvailableProducts() throws Exception {
        this.product.setSellStart(LocalDateTime.now().minusHours(10));
        when(repository.findAllBySellStartBefore(any(LocalDateTime.class))).thenReturn(Collections.singletonList(this.product));

        assertEquals(Collections.singletonList(this.product), service.getAvailableProducts());
    }

    /**
     * Method testGetAvailableProducts ...
     *
     * @throws Exception when
     */
    @Test
    public void testGetAvailableProductsEmpty() throws Exception {
        this.product.setSold(100);
        this.product.setMaxSold(100);
        when(repository.findAllBySellStartBefore(any(LocalDateTime.class))).thenReturn(Collections.singletonList(this.product));

        assertEquals(Collections.emptyList(), service.getAvailableProducts());
    }

    /**
     * Test get product by key
     *
     * @throws Exception when..
     */
    @Test
    public void testGetByKey() throws Exception {
        when(repository.findByKey(this.product.getKey())).thenReturn(Optional.of(this.product));

        assertEquals(this.product, service.getByKey(this.product.getKey()));
    }

    /**
     * Test get product by key empty
     *
     * @throws Exception when..
     */
    @Test
    public void testGetByKeyEmpty() throws Exception {
        thrown.expect(ProductNotFoundException.class);
        thrown.expectMessage("Product with key " + this.product.getKey() + " not found!");

        when(repository.findByKey(this.product.getKey())).thenReturn(Optional.empty());
        service.getByKey(this.product.getKey());
    }

    /**
     * Test update
     *
     * @throws Exception when..
     */
    @Test
    public void testUpdate() throws Exception {
        when(repository.findByKey(this.product.getKey())).thenReturn(Optional.of(this.product));

        service.update(this.product);
        verify(repository, times(1)).save(this.product);
    }

    /**
     * Test update empty
     *
     * @throws Exception when
     */
    @Test
    public void testUpdateEmpty() throws Exception {
        thrown.expect(ProductNotFoundException.class);
        thrown.expectMessage("Product with key " + this.product.getKey() + " not found!");

        when(repository.findByKey(this.product.getKey())).thenReturn(Optional.empty());

        service.update(this.product);
    }

    /**
     * Test create
     *
     * @throws Exception when
     */
    @Test
    public void testCreate() throws Exception {
        service.create(this.product);
        verify(repository, times(1)).saveAndFlush(this.product);
    }

    /**
     * Test delete
     *
     * @throws Exception when
     */
    @Test
    public void testDelete() throws Exception {
        service.delete(this.product);
        verify(repository, times(1)).delete(this.product);
    }

    /**
     * Test delete when the product is added to an event
     *
     * @throws Exception when
     */
    @Test
    public void testDeleteProductAddedToEvent() throws Exception {
        thrown.expect(ProductAlreadyLinkedException.class);
        thrown.expectMessage("Product is already added to an Event");

        this.product.setLinked(true);

        service.delete(this.product);
    }

}