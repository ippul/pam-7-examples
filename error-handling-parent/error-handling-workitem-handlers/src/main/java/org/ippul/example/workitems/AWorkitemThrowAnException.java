package org.ippul.example.workitems;

import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;

import java.util.HashMap;
import java.util.Map;

public class AWorkitemThrowAnException extends AbstractLogOrThrowWorkItemHandler {

    public AWorkitemThrowAnException(String processId, String strategy) {
        super();
        this.handlingProcessId = processId;
        this.handlingStrategy = strategy;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        final Map<String, Object> result = new HashMap<String, Object>();
        if (workItem.getParameter("errorFixed") == null) {
            System.out.println("processId: " + handlingProcessId);
            System.out.println("strategy: " + handlingStrategy);
            handleException(new RuntimeException("On purpose"));
        } else {
            result.put("Message", workItem.getParameter("errorFixed"));
        }
        workItemManager.completeWorkItem(workItem.getId(), result);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        System.out.println("Abort workitem!");
        workItemManager.abortWorkItem(workItem.getId());
    }
}
