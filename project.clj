(defproject clj-firebase "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [com.google.firebase/firebase-admin "4.1.7"]]
  :main ^:skip-aot clj-firebase.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
