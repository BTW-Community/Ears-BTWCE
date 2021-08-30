package com.unascribed.ears.common.debug;

import java.util.Set;

import org.teavm.jso.JSBody;

class EarsLogImpl {

	@JSBody(script="return window.EarsDebug")
	static native boolean checkDebug();
	
	static Set<String> checkOnlyDebug() {
		return null;
	}
	
	// String.format pulls in a lot of stuff we do not need, so reimplement it for JS target
	// this saves 200K without minification and 100K after minification (!)
	@JSBody(params={"secs", "millis", "tag", "msg"},
			script="return \"[T+\"+(\"000\"+secs).slice(-3)+\".\"+(\"000\"+millis).slice(-3)+\"] (\"+tag+\"): \"+msg")
	static native String buildMsg(int secs, int millis, String tag, String msg);
	
	@JSBody(params={"msg"}, script="console.debug(msg)")
	static native void log(String msg);
	
}
