package paytenfood.bot.controller;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paytenfood.bot.util.HttpUtil;
import paytenfood.bot.util.KeyboardUtil;
import ru.multicon.viber4j.ViberBot;
import ru.multicon.viber4j.ViberBotManager;
import ru.multicon.viber4j.account.UserDetails;
import ru.multicon.viber4j.incoming.Incoming;
import ru.multicon.viber4j.incoming.IncomingImpl;
import ru.multicon.viber4j.keyboard.ViberButton;
import ru.multicon.viber4j.keyboard.ViberKeyboard;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    @Value("${viber.token}")
    private String botToken;
    @Value("${viber.web-hook}")
    private String webHookUrl;
    @Value("${viber.media-source-url}")
    private String mediaSourceUrl;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;
    private ArrayList<String> orderedItems = new ArrayList<>();
    private int price = 0;
    boolean placam = false;
    private final String MESSAGE_EVENT = "message";
    private final String START_MSG_EVENT = "conversation_started";

        @RequestMapping(method = POST, path = "/viberbot")
        ResponseEntity<?> callbackHandle(@RequestBody String text) throws IOException, InterruptedException {
            logger.info("Received messageForUser {}", text);
            // Processing incoming messageForUser
            Incoming incoming = IncomingImpl.fromString(text);
            String eventType = incoming.getEvent();
            logger.info("Event type {}", eventType);
            if (!StringUtils.equals(eventType, MESSAGE_EVENT)
                    && !StringUtils.equals(incoming.getEvent(), START_MSG_EVENT))
                return ResponseEntity.ok().build();

            String userName = incoming.getSenderName();
            String userId = incoming.getSenderId();
            String messageText = incoming.getMessageText();
            logger.info("Message text: {}", messageText);
            //
            // Viber bot instance
            //
            ViberBot bot = ViberBotManager.viberBot(botToken);

            //
            // getting info about user
            //
            UserDetails userDet = bot.getUserDetails(userId);
            logger.info("User country: {}", userDet.getCountry());
            logger.info("User device: {}", userDet.getDeviceType());


            if (StringUtils.equals("start", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText("Dobrodošli u Viber bot Dental Care ordinacije! Možete zakazati naše usluge praćenjem upustva na ekranu.", keyboard);
            }
            else if (StringUtils.equals("0", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText("Povratak na glavni meni.", keyboard);
            }
            else if (StringUtils.equals("1", messageText)) {
                ViberKeyboard keyboard = createPizzaMenu();
                bot.messageForUser(userId).postText("Prikazujem listu usluga pregleda.", keyboard);
            }
            else if (StringUtils.equals("2", messageText)) {
                ViberKeyboard keyboard = createBurgerMenu();
                bot.messageForUser(userId).postText("Prikazujem listu usluga bolesti zuba.", keyboard);
            }
            else if (StringUtils.equals("3", messageText)) {

                ViberKeyboard keyboard = changeOrder();
                bot.messageForUser(userId).postText("Prikazujem listu izabranih usluga.", keyboard);
            }
            else if (StringUtils.equals("4", messageText)) {
                ViberKeyboard keyboard = createSpecial();
                bot.messageForUser(userId).postText("Prikazujem listu usluga oralne hirurgije.", keyboard);
            }
            else if (StringUtils.equals("5", messageText)) {
                ViberKeyboard keyboard = createDesertMenu();
                bot.messageForUser(userId).postText("Prikazujem listu usluga decije stomatologije.", keyboard);
            }
            else if (StringUtils.equals("6", messageText)) {
                ViberKeyboard keyboard = createFinish();
                bot.messageForUser(userId).postText("Zavrsite porudzbinu.", keyboard);
            }
            else if (StringUtils.equals("15", messageText)) {
                bot.messageForUser(userId).postText("Izaberite vreme u formatu SAT:MIN, primer 16:30");
                placam = true;
            }
            else if(placam){
            String orderList ="";
            for(int i =0;i<orderedItems.size();i++){
                orderList+=orderedItems.get(i)+" ";
            }

            String date = LocalDate.now().toString()+"T"+messageText;
            String url = "http://food-rest:9097/api/v1/addOrder";
            String body = String.format("{\"description\":\"%s\",\"price\":%d,\"pickupTime\":\"%s\",\"creationTime\":\"%s\",\"state\":\"%s\",\"viberID\":\"%s\"}",orderList,price,date,LocalDateTime.now().toString(),"IN_PROGRESS",userName);

            HttpRequest requst = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(requst, HttpResponse.BodyHandlers.ofString());
            logger.info(response.body());
            //PROVERI DA LI JE response.status 200
            bot.messageForUser(userId).postText("Vasa narudzbina je prihvacena!");
            placam = false;
            orderedItems.clear();
            price = 0;
            }
            //MENI
            else if (StringUtils.equals("7", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=250;
                orderedItems.add("Kapricoza");
                bot.messageForUser(userId).postText("Kapricoza je dodata na vas racun!.", keyboard);
            }
            else if (StringUtils.equals("8", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=250;
                orderedItems.add("Margarita");
                bot.messageForUser(userId).postText("Margarita je dodata na vas racun!.", keyboard);
            }
            else if (StringUtils.equals("9", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=250;
                orderedItems.add("Hamburger");
                bot.messageForUser(userId).postText("Hamburger je dodat na vas racun!.", keyboard);
            }
            else if (StringUtils.equals("10", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=200;
                orderedItems.add("Cizburger");
                bot.messageForUser(userId).postText("Cizburger je dodat na vas racun!.", keyboard);
            }
            else if (StringUtils.equals("11", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=150;
                orderedItems.add("Baklava");
                bot.messageForUser(userId).postText("Baklava je dodat na vas racun!.", keyboard);
            }
            else if (StringUtils.equals("12", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=100;
                orderedItems.add("Sladoled");
                bot.messageForUser(userId).postText("Sladoled je dodat na vas racun!.", keyboard);
            }
            else if (StringUtils.equals("13", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=450;
                orderedItems.add("Sarma");
                bot.messageForUser(userId).postText("Sarma je dodat na vas racun!.", keyboard);
            }
            else if (StringUtils.equals("14", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                price+=500;
                orderedItems.add("Gulas");
                bot.messageForUser(userId).postText("Gulas je dodat na vas racun!.", keyboard);
            }
            else {
                ViberKeyboard keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText("Komanda nije pronađena, povratak na start meni.", keyboard);
            }
            return ResponseEntity.ok().build();
        }
        private ViberKeyboard createPizzaMenu(){
                return (ViberKeyboard) new ViberKeyboard().
                addButton(
                        new ViberButton("7").
                        setText("<br><b>STOMATOLOŠKI PREGLED</b><br>OPIS: Standardni stomatološki pregled i konsultacije sa stomatologom. <br>CENA: 2.500,00RSD").setColumns(4).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.LEFT)).
                addButton(
                        new ViberButton("8").
                        setImage("https://sputnik-it.rs/images/check-mark-final.png").setText("<br><br><br><font color=\"#494E67\"><b>REZERVIŠI</b></font>").setColumns(2).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE)).
                addButton(
                        new ViberButton("7").
                        setText("<br><b>SPECIJALISTIČKI PREGLED</b><br>OPIS: Pregled kod stomatologa specijaliste u cilju dijagnostifikovanja potrebne stomatološke intervencije. <br>CENA: 4.000,00RSD").setColumns(4).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.LEFT)).
                addButton(
                        new ViberButton("8").
                        setImage("https://sputnik-it.rs/images/check-mark-final.png").setText("<br><br><br><font color=\"#494E67\"><b>REZERVIŠI</b></font>").setColumns(2).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE)).
                addButton(
                        new ViberButton("0").
                        setText("<b>POVRATAK NA GLAVNI MENI</b>").setTextSize(ViberButton.TextSize.LARGE).setBgColor("#7eceea"));
        }
        private ViberKeyboard createBurgerMenu(){
                return (ViberKeyboard) new ViberKeyboard().setButtonsGroupColumns(2).setButtonsGroupRows(3).
                addButton(
                        new ViberButton("9").
                        setText("Hamburger ... 250 RSD")).
                addButton(
                        new ViberButton("10").
                        setText("Cizburger ... 200 RSD")).
                addButton(
                                new ViberButton("0").
                                setText("Povratak na glavni meni"));
        }
        private ViberKeyboard createDesertMenu(){
                return (ViberKeyboard) new ViberKeyboard().setButtonsGroupColumns(2).setButtonsGroupRows(3).
                addButton(
                        new ViberButton("11").
                        setText("Baklava ... 150 RSD")).
                addButton(
                        new ViberButton("12").
                        setText("Sladoled ... 100 RSD")).
                addButton(
                                new ViberButton("0").
                                setText("Povratak na glavni meni"));
        }
        private ViberKeyboard createSpecial(){
                return (ViberKeyboard) new ViberKeyboard().setButtonsGroupColumns(2).setButtonsGroupRows(3).
                addButton(
                        new ViberButton("13").
                        setText("Sarma ... 450 RSD")).
                addButton(
                        new ViberButton("14").
                        setText("Gulaš ... 500 RSD")).
                addButton(
                                new ViberButton("0").
                                setText("Povratak na glavni meni"));
        }
        private ViberKeyboard changeOrder(){
                ViberKeyboard viber = new ViberKeyboard();
                for(int i =0;i<orderedItems.size();i++){
                        viber.addButton(new ViberButton("NONE").setText(orderedItems.get(i)));
                }
                viber.addButton(new ViberButton("NONE").setText("UKUPNO ZA UPLATU: "+ Integer.toString(price)));
                viber.addButton(new ViberButton("0").setText("POVRATAK NA MENI!"));
                return viber;
        }
        private ViberKeyboard createFinish(){
                return (ViberKeyboard) new ViberKeyboard().setButtonsGroupColumns(2).setButtonsGroupRows(3).
                addButton(
                        new ViberButton("15").
                        setText("Izaberite vreme!")).
                addButton(
                                new ViberButton("0").
                                setText("Povratak na glavni meni"));
        }

        private ViberKeyboard createStartKeyboard() {
            return keyboardUtil.getMainMenu();
        }

    }
        

