package ch.wisv.events.core.service.product;

import ch.wisv.events.core.exception.normal.SoldProductNotFoundException;
import ch.wisv.events.core.exception.runtime.SoldProductDoesNotContainProductException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.SoldProductRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
@Service
public class SoldProductServiceImpl implements SoldProductService {

    /**
     * Field this.soldProductRepository
     */
    private final SoldProductRepository soldProductRepository;

    /**
     * Constructor SoldProductServiceImpl creates a new SoldProductServiceImpl instance.
     *
     * @param soldProductRepository of type SoldProductRepository
     */
    @Autowired
    public SoldProductServiceImpl(SoldProductRepository soldProductRepository) {
        this.soldProductRepository = soldProductRepository;
    }

    /**
     * Get SoldProduct by key
     *
     * @return SoldProduct
     */
    @Override
    public SoldProduct getByKey(String key) throws SoldProductNotFoundException {
        return this.soldProductRepository.findByKey(key)
                .orElseThrow(() -> new SoldProductNotFoundException("SoldProduct with key " + key + " is not exists!"));
    }

    /**
     * Method getAll returns the all of this SoldProductService object.
     *
     * @return the all (type List<SoldProduct>) of this SoldProductService object.
     */
    @Override
    public List<SoldProduct> getAll() {
        return this.soldProductRepository.findAll();
    }

    /**
     * Method getByProduct find sold products by product.
     *
     * @param product of type Product
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getByProduct(Product product) {
        return this.soldProductRepository.findAllByProduct(product);
    }

    /**
     * Method getAllByCustomerAndProduct find sold products by customer and products.
     *
     * @param customer of type Customer
     * @param product  of type Product
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getAllByCustomerAndProduct(Customer customer, Product product) {
        return this.soldProductRepository.findAllByProductAndCustomer(product, customer);
    }

    /**
     * Method getByCustomer find sold products by customer
     *
     * @param customer of type Customer
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getByCustomer(Customer customer) {
        return this.soldProductRepository.findAllByCustomer(customer);
    }

    /**
     * Method create ...
     *
     * @param order of type Order
     */
    @Override
    public List<SoldProduct> create(Order order) {
        List<SoldProduct> soldProducts = new ArrayList<>();

        order.getOrderProducts().forEach(orderProduct -> {
            for (int i = 0; i < orderProduct.getAmount(); i++) {
                SoldProduct soldProduct = new SoldProduct(orderProduct.getProduct(), order, order.getCustomer());
                soldProduct.setUniqueCode(this.determineUniqueCode(soldProduct));

                soldProductRepository.saveAndFlush(soldProduct);
                soldProducts.add(soldProduct);
            }
        });

        return soldProducts;
    }

    /**
     * Method determineUniqueCode ...
     *
     * @param sold of type SoldProduct
     * @return String
     */
    private String determineUniqueCode(SoldProduct sold) {
        String uniqueCode = this.generateUniqueCode();

        while (!this.determineUniquenessCode(sold, uniqueCode)) {
            uniqueCode = this.generateUniqueCode();
        }

        return uniqueCode;
    }

    /**
     * Method generateUniqueCode ...
     *
     * @return String
     */
    private String generateUniqueCode() {
        return RandomStringUtils.randomNumeric(6);
    }

    /**
     * Method delete ...
     *
     * @param order of type Order
     */
    @Override
    public void delete(Order order) {
        List<SoldProduct> soldProducts = this.soldProductRepository.findAllByOrder(order);

        this.soldProductRepository.delete(soldProducts);
    }

    /**
     * Update sold product
     *
     * @param soldProduct of type SoldProduct
     */
    @Override
    public void update(SoldProduct soldProduct) throws SoldProductNotFoundException {
        SoldProduct model = this.getByKey(soldProduct.getKey());

        model.setOrder(soldProduct.getOrder());
        model.setStatus(soldProduct.getStatus());
        model.setProduct(soldProduct.getProduct());
        model.setCustomer(soldProduct.getCustomer());

        this.soldProductRepository.save(soldProduct);
    }

    /**
     * Method getAllByEvent ...
     *
     * @param event of type Event
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getAllByEvent(Event event) {
        List<SoldProduct> soldProducts = new ArrayList<>();
        event.getProducts().forEach(product -> soldProducts.addAll(this.getByProduct(product)));

        return soldProducts;
    }

    /**
     * Method determineUniquenessCode ...
     *
     * @param soldProduct of type SoldProduct
     * @param uniqueCode  of type String
     * @return boolean
     */
    private boolean determineUniquenessCode(SoldProduct soldProduct, String uniqueCode) {
        if (soldProduct.getProduct() == null) {
            throw new SoldProductDoesNotContainProductException();
        }

        Optional<SoldProduct> optional = this.soldProductRepository.findByProductAndUniqueCode(
                soldProduct.getProduct(),
                uniqueCode
        );

        return !optional.isPresent();
    }
}
