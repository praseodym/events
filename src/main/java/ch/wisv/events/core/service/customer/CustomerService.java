package ch.wisv.events.core.service.customer;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.order.Customer;

import java.time.LocalDateTime;
import java.util.List;

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
public interface CustomerService {

    /**
     * Get all customers.
     *
     * @return list of all customers
     */
    List<Customer> getAllCustomers();

    /**
     * Method getAllCustomerCreatedAfter ...
     *
     * @param after of type LocalDateTime
     * @return List<Customer>
     */
    List<Customer> getAllCustomerCreatedAfter(LocalDateTime after);

    /**
     * Get a customer by key.
     *
     * @param key key
     * @return Customer
     */
    Customer getByKey(String key) throws CustomerNotFoundException;

    /**
     * Get a customer by CH username or Email.
     *
     * @param username of type String
     * @param email    of type String
     * @return Customer
     */
    Customer getByChUsernameOrEmail(String username, String email) throws CustomerNotFoundException;

    /**
     * Get a customer by rfidToken.
     *
     * @param query of type String
     * @return Customer
     */
    Customer getByRfidToken(String query) throws CustomerNotFoundException;

    /**
     * Add a new customer.
     *
     * @param customer customer model
     */
    void create(Customer customer) throws CustomerInvalidException;

    /**
     * Add a new customer by ChUserInfo.
     *
     * @param userInfo of type CHUserInfo
     */
    Customer createByChUserInfo(CHUserInfo userInfo) throws CustomerInvalidException;

    /**
     * Update a existing customer.
     *
     * @param customer customer model
     */
    void update(Customer customer) throws CustomerInvalidException, CustomerNotFoundException;

    /**
     * Delete a customer.
     *
     * @param customer customer model
     */
    void delete(Customer customer);
}
