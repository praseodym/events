package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
public interface WebhookTaskRepository extends JpaRepository<WebhookTask, Integer> {

    /**
     * Method findAllOrderBy_CreatedAt_Asc ...
     *
     * @return List<WebhookTask>
     */
    List<WebhookTask> findAllByOrderByCreatedAtAsc();

    /**
     * Method findAllByWebhookTaskStatus().
     *
     * @param webhookTaskStatus of type WebhookTaskStatus
     * @return List<WebhookTask>
     */
    List<WebhookTask> findAllByWebhookTaskStatus(WebhookTaskStatus webhookTaskStatus);

}
