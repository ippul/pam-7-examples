package org.ippul.example.error.handling;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ExampleRunnerTest {
    private static final String username = "rhpamAdmin";
    private static final String password = "Pa$$w0rd";
    private static final String url = "http://localhost:8080/kie-server/services/rest/server";
    private static final String containerId = "error-handling-kjar_1.0";

    public KieServicesClient getKieServicesClient() {
        final KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, username, password);
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(100000l);
        return KieServicesFactory.newKieServicesClient(config);
    }

    @Before
    public void before(){
        final KieContainerResource kieContainerResources = new KieContainerResource(containerId, new ReleaseId("org.ippul.example", "error-handling-kjar" , "1.0"));
        getKieServicesClient().createContainer(containerId, kieContainerResources);
    }

    @After
    public void after() {
        final ProcessServicesClient processServicesClient = getKieServicesClient().getServicesClient(ProcessServicesClient.class);
        final List<ProcessInstance> processInstances = processServicesClient.findProcessInstances(containerId, 0, 10000);
        for(ProcessInstance processInstance : processInstances) {
            if(processInstance.getParentId() == -1) {
                processServicesClient.abortProcessInstance(containerId, processInstance.getId());
            }
        }
        getKieServicesClient().deactivateContainer(containerId);
        getKieServicesClient().disposeContainer(containerId);
    }


    @Test
    public void processWithErrorHandlingTest() throws InterruptedException {
        final ProcessServicesClient processServicesClient = getKieServicesClient().getServicesClient(ProcessServicesClient.class);
        final UserTaskServicesClient userTaskServicesClient = getKieServicesClient().getServicesClient(UserTaskServicesClient.class);
        final QueryServicesClient queryServicesClient = getKieServicesClient().getServicesClient(QueryServicesClient.class);
        final Long processInstanceId = processServicesClient.startProcess(containerId, "service-exception-handler-kjar.error-throwing-process");
        final List<ProcessInstance> processInstancesByParent = processServicesClient.findProcessInstancesByParent(containerId, processInstanceId, 0, 1000);
        {// first retry
            final Optional<ProcessInstance> first = processInstancesByParent.stream().filter(processInstance ->
                    processInstance.getProcessId().equals("service-exception-handler-kjar.error-handling-process")).findFirst();
            Assert.assertTrue(first.isPresent());
        }
        Thread.sleep(5000l);
        {// second retry
            final Optional<ProcessInstance> first = processInstancesByParent.stream().filter(processInstance ->
                    processInstance.getProcessId().equals("service-exception-handler-kjar.error-handling-process")).findFirst();
            Assert.assertTrue(first.isPresent());
        }
        Thread.sleep(5000l);
        {// Third retry
            final Optional<ProcessInstance> first = processInstancesByParent.stream().filter(processInstance ->
                    processInstance.getProcessId().equals("service-exception-handler-kjar.error-handling-process")).findFirst();
            Assert.assertTrue(first.isPresent());
        }
        Thread.sleep(7000l);
        final List<TaskSummary> tasksAssignedAsPotentialOwner = userTaskServicesClient.findTasksAssignedAsPotentialOwner(username, 0, 10000);
        userTaskServicesClient.claimTask(containerId, tasksAssignedAsPotentialOwner.get(0).getId(),username);
        userTaskServicesClient.startTask(containerId, tasksAssignedAsPotentialOwner.get(0).getId(),username);
        final Map<String, Object> taskParameters = new HashMap<String, Object>();
        taskParameters.put("errorFixed", "Error has been fixed manually!");
        userTaskServicesClient.completeTask(containerId, tasksAssignedAsPotentialOwner.get(0).getId(),username, taskParameters);
        final ProcessInstance processInstanceById = queryServicesClient.findProcessInstanceById(processInstanceId);
        assertEquals(Integer.valueOf(2), processInstanceById.getState());
    }

}
