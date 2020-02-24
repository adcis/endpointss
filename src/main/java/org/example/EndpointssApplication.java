package org.example;

import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.EndpointsList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class EndpointssApplication {
    final static Logger log = LoggerFactory.getLogger(EndpointssApplication.class);

    private final static AtomicBoolean repeat = new AtomicBoolean(true);

    public static void main(String[] args) {
        try (KubernetesClient kubernetesClient = new DefaultKubernetesClient()) {

            SharedInformerFactory sharedInformerFactory = kubernetesClient.informers();

            SharedIndexInformer<Endpoints> serviceSharedIndexInformer = sharedInformerFactory.sharedIndexInformerFor(Endpoints.class, EndpointsList.class, 1000);

            serviceSharedIndexInformer.addEventHandler(new ResourceEventHandler<Endpoints>() {
                @Override
                public void onAdd(Endpoints endpoints) {
                    log.info("Added {}", endpoints.getMetadata().getName());
                }

                @Override
                public void onUpdate(Endpoints updated, Endpoints by) {
                    log.info("Updated {} by {}", updated.getMetadata().getName(), by.getMetadata().getName());
                }

                @Override
                public void onDelete(Endpoints endpoints, boolean deletedFinalStateUnknown) {
                    log.info("Deleted {}", endpoints.getMetadata().getName());
                }
            });

            sharedInformerFactory.startAllRegisteredInformers();

            while (!serviceSharedIndexInformer.hasSynced()) {
                Thread.sleep(100);
            }

            while (repeat.get()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            log.info("Interrupted");
        }
    }
}
