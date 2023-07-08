package com.example.emailservice.kafka;

import com.example.basedomain.entity.Order;
import com.example.basedomain.entity.OrderEvent;
import com.example.emailservice.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    @Autowired
    private EmailSenderService senderService;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            , groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(OrderEvent event) throws Exception {
        LOGGER.info(String.format("Order event received in email service => %s", event.toString()));
        senderService.send(
                "quocbao642002@gmail.com",
                buildEmail(event.getOrder(), event.getStatus()));
    }

    @Bean
    public StringJsonMessageConverter jsonConverter() {
        return new StringJsonMessageConverter();
    }

    private String buildEmail(Order order, String status) {
        String clone_HtmlMsgHeader = "" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "   <tr>\n" +
                "      <td bgcolor=\"#f4f4f4\" align=\"center\">\n" +
                "         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "            <tr>\n" +
                "               <td align=\"center\" valign=\"top\" style=\"padding: 40px 10px 40px 10px;\"> </td>\n" +
                "            </tr>\n" +
                "         </table>\n" +
                "      </td>\n" +
                "   </tr>\n" +
                "   <tr>\n" +
                "      <td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                "         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "            <tr>\n" +
                "               <td bgcolor=\"#ffffff\" align=\"center\" valign=\"top\" style=\"padding: 40px 20px 20px 20px; border-radius: 4px 4px 0px 0px; color: #111111; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 48px; font-weight: 400; letter-spacing: 4px; line-height: 48px;\">\n" +
                "                  <h1 style=\"font-size: 48px; font-weight: 400; margin: 2;\">Thank You For Your Order!</h1>\n" +
                "                  <img src=\" https://img.icons8.com/clouds/100/000000/handshake.png\" width=\"125\" height=\"120\" style=\"display: block; border: 0px;\" />\n" +
                "               </td>\n" +
                "            </tr>\n" +
                "         </table>\n" +
                "      </td>\n" +
                "   </tr>\n";

        String clone_HtmlMsgFooter = "" +
                "    <tr>\n" +
                "        <td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 10px 0px 10px\">\n" +
                "            <table\n" +
                "                border=\"0\"\n" +
                "                cellpadding=\"0\"\n" +
                "                cellspacing=\"0\"\n" +
                "                width=\"100%\"\n" +
                "                style=\"max-width: 600px\"\n" +
                "            >\n" +
                "                <tr>\n" +
                "                    <td\n" +
                "                        bgcolor=\"#ffffff\"\n" +
                "                        align=\"left\"\n" +
                "                        style=\"\n" +
                "                            padding: 20px 30px 20px 30px;\n" +
                "                            color: #666666;\n" +
                "                            font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                            font-size: 18px;\n" +
                "                            font-weight: 400;\n" +
                "                            line-height: 25px;\n" +
                "                        \"\n" +
                "                    >\n" +
                "                        <p style=\"margin: 0\">\n" +
                "                            If you have any questions, just reply to this\n" +
                "                            email—we're always happy to help out.\n" +
                "                        </p>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td\n" +
                "                        bgcolor=\"#ffffff\"\n" +
                "                        align=\"left\"\n" +
                "                        style=\"\n" +
                "                            padding: 0px 30px 40px 30px;\n" +
                "                            border-radius: 0px 0px 4px 4px;\n" +
                "                            color: #666666;\n" +
                "                            font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                            font-size: 18px;\n" +
                "                            font-weight: 400;\n" +
                "                            line-height: 25px;\n" +
                "                        \"\n" +
                "                    >\n" +
                "                        <p style=\"margin: 0\">Cheers,<br />My Team</p>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>";

        String bodyHtml = "" +
                "   <tr>\n" +
                "      <td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 10px 0px 10px;\">\n" +
                "         <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "            <tr>\n" +
                "               <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 10px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                  <p style=\"margin: 0;\">We’re happy to let you know that we’ve received your order.</p>\n" +
                "               </td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 10px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                  <p style=\"margin: 0;\">Once your package ships, we will send you an email with a tracking number and link so you can see the movement of your package.</p>\n" +
                "               </td>\n" +
                "           </tr>\n" +
                "            <tr>\n" +
                "               <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 10px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                  <p style=\"margin: 0;\">If you have any questions, contact us here or call us on [contact number]!</p>\n" +
                "               </td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <td bgcolor=\"#ffffff\" align=\"left\" style=\"padding: 20px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">\n" +
                "                  <p style=\"margin: 0;\">P.S. psst… you may love these too:</p>\n" +
                "               </td>\n" +
                "           </tr>\n" +
                "           <tr>\n" +
                "               <table style=\"width: 600px;border: 2px solid #aba7a7\">\n" +
                "                   <tr style=\"border: 2px solid #aba7a7\">\n" +
                "                       <th bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 600; line-height: 25px;\">Product Name</th>\n" +
                "                       <th bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 600; line-height: 25px;\">Quantity</th>\n" +
                "                       <th bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 600; line-height: 25px;\">Total Price</th>\n" +
                "                       <th bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 600; line-height: 25px;\">Status</th>\n" +
                "                  </tr>\n" +
                "                  <tr style=\"border: 2px solid #aba7a7\">\n" +
                "                    <td bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">" + order.getName() + "</td>\n" +
                "                    <td bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">" + order.getQty() + "</td>\n" +
                "                    <td bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">" + order.getPrice() + "</td>\n" +
                "                    <td bgcolor=\"#ffffff\" align=\"center\" style=\"color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 18px; font-weight: 400; line-height: 25px;\">" + status + "</td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "           </tr>\n" +
                "         </table>\n" +
                "      </td>\n" +
                "   </tr>\n";

        return clone_HtmlMsgHeader + bodyHtml + clone_HtmlMsgFooter;
    }
}