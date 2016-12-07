package live.connector.vertxui.core;
//
// import io.vertx.core.json.JsonObject;
//
// public class GUI extends VertxUI {
//
// // Model
// public class Model {
// public String user;
// public String password;
// }
//
// private Model model = new Model();
//
// private Html title;
//
// public GUI() {
//
// // View
// title = docRoot().div("<h1>Bla</h1>");
// Html form = docRoot().form();
// form.input("user", model.user, i -> {
// model.user = i;
// }).input("password", i -> {
// model.password = i;
// }).input("SEND", null, () -> {
// form.send();
// });
//
// // View-Controller binding
// form.post(GUI::sent);
// eventBus().register("response",GUI::receive);
// }
//
// // Controller
// private void sent(JsonObject updated) {
// eventBus().wrap("login", updated);
// title.inner("<h1>Sent!</h1>");
// }
//
// private void receive(JsonObject received) {
// title.inner("<h1>Sent!</h1>");
//
// }
//
// }
