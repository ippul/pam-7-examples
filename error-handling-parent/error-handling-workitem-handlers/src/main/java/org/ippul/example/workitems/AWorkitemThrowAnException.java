package org.ippul.example.workitems;

import org.ippul.example.workitems.exceptions.AutomaticRetryException;
import org.ippul.example.workitems.exceptions.HumanActionNeededException;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;

import java.util.HashMap;
import java.util.Map;

public class AWorkitemThrowAnException extends AbstractLogOrThrowWorkItemHandler {

    private Integer maxAutomaticRetry;

    public AWorkitemThrowAnException(String processId, String strategy, Integer maxAutomaticRetry) {
        super();
        this.handlingProcessId = processId;
        this.handlingStrategy = strategy;
        this.maxAutomaticRetry = maxAutomaticRetry;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        if(workItem.getParameter("retryCount") == null){
            workItem.getParameters().put("retryCount", 0);
        }
        final Integer retryCount = (Integer) workItem.getParameter("retryCount");
        final Map<String, Object> result = new HashMap<String, Object>();
        if (retryCount <= maxAutomaticRetry) {
            System.out.println("Retry count: " + retryCount);
            cleanWorkitemParameter(workItem);
            handleException(new AutomaticRetryException("Automatic retry exception [retry: " + retryCount + "]"));
        } else if (retryCount > maxAutomaticRetry && (workItem.getParameter("errorFixed") == null || ((String)workItem.getParameter("errorFixed")).equals(""))) {
            System.out.println("Too many retry. Need Human Fix");
            cleanWorkitemParameter(workItem);
            handleException(new HumanActionNeededException("Error during execution please fix it manually"));
        } else {
            result.put("Message", workItem.getParameter("errorFixed"));
        }
        workItemManager.completeWorkItem(workItem.getId(), result);
    }

    private void cleanWorkitemParameter(WorkItem workItem) {
        workItem.getParameters().remove("DeploymentId");
        workItem.getParameters().remove("ProcessInstanceId");
        workItem.getParameters().remove("WorkItemId");
        workItem.getParameters().remove("NodeInstanceId");
        workItem.getParameters().remove("ErrorMessage");
        workItem.getParameters().remove("Error");
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
        System.out.println("Abort workitem!");
        workItemManager.abortWorkItem(workItem.getId());
    }
}
