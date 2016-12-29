package live.connector.vertxui.server;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class FigWheely {

	// TODO refactor: put javascript below here in Java code
	public static final String script = "console.log('FigWheely started');new WebSocket('ws://localhost:"
			+ FigWheelyVertX.port
			+ "').onmessage = function(m) {console.log(m.data);removejscssfile(m.data.substr(8));};                                          "
			+ "function removejscssfile(filename){                \n"
			+ "if (filename.endsWith('js')) filetype='js'; else filetype='css';         "
			+ "var el = (filetype=='js')? 'script':'link';                                             \n"
			+ "var attr = (filetype=='js')? 'src':'href';                                  \n"
			+ "var all =document.getElementsByTagName(el);                               \n"
			+ "for (var i=all.length; i>=0; i--) {                                        \n"
			+ "   if (all[i] && all[i].getAttribute(attr)!=null && all[i].getAttribute(attr).indexOf(filename)!=-1) {"
			+ "       var parent=all[i].parentNode;                                          \n"
			+ "       parent.removeChild(all[i]);                                        \n"
			+ "       var script = document.createElement(el);                         \n"
			+ "		  if (filetype=='js') {                                                  \n"
			+ "      	  script.type='text/javascript'; script.src=filename;"
			+ "       } else {                                                        "
			+ "      	  script.rel='stylesheet'; script.href=filename+'?'+(new Date().getTime());"
			+ "       }                                                                     "
			+ "       parent.appendChild(script);   	                 \n" + "  }  } };                           ";

	/**
	 * Server bootstrap.
	 * 
	 * @param router
	 *            the router which will be watched
	 */
	public static void with(Router router) {
		if (FigWheelyVertX.started) {
			throw new IllegalArgumentException("Can only start once");
		}
		FigWheelyVertX.started = true;
		Vertx.currentContext().owner().deployVerticle(FigWheelyVertX.class.getName());
		FigWheelyVertX.router = router;
	}

}
