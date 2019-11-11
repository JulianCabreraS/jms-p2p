package com.bharath.jms.hm.clinicals.com.bharath.jms.hm.eligibilitycheck;

import com.bharath.jms.hm.model.Patient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EligibilityCheckListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;

        try (
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
                JMSContext jmsContext = connectionFactory.createContext()) {
            InitialContext initialContext = new InitialContext();
            Queue replyQueue = (Queue) initialContext.lookup("queue/replyQueue");
            MapMessage replyMessage = jmsContext.createMapMessage();
            Patient patient = (Patient) objectMessage.getObject();
            String insuranceProvider = patient.getInsuranceProvider();
            System.out.println("Insurance Provider: "+insuranceProvider);
            if(insuranceProvider.equals("Blue Cross Blue Shield") || insuranceProvider.equals("United Health")){
                if(patient.getCopay() <40 && patient.getAmountToBePayed() < 1000){
                    replyMessage.setBoolean("eligible", true);
                }
                else {
                    replyMessage.setBoolean("eligible", false   );
                }
                JMSProducer producer = jmsContext.createProducer();
                producer.send(replyQueue,replyMessage);
            }
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        }
    }
}
