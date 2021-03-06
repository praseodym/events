package ch.wisv.events.core.model.order;

import lombok.Getter;

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
public enum OrderStatus {

    OPEN(false),
    PAID_CASH(true),
    PAID_PIN(true),
    PAID_IDEAL(true),
    REJECTED(false),
    CANCELLED(false),
    REFUNDED(false),
    CLOSED(false),
    WAITING(false);

    @Getter
    private final boolean paid;

    OrderStatus(boolean paid) {
        this.paid = paid;
    }
}
