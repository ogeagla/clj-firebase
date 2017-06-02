(ns clj-firebase.core
  #_(:require [com.google.firebase.database :refer :all])
  (:require [clojure.java.io :as io]
            [clojure.core.reducers :as r])
  (:import (com.google.firebase FirebaseOptions FirebaseOptions$Builder FirebaseApp)
           (com.google.firebase.auth FirebaseCredential FirebaseCredentials)
           (java.io FileInputStream)
           (com.google.firebase.database FirebaseDatabase DatabaseReference ValueEventListener DataSnapshot DatabaseError)))

#_(defn init []
    (let [opts (.Builder (FirebaseOptions.))])
    )

#_(def db
  (with-open
    [service-account (io/input-stream
                       "service-account.json")]
    (let [opts (->
                 (FirebaseOptions$Builder.)
                 (.setCredential (FirebaseCredentials/fromCertificate service-account))
                 (.setDatabaseUrl "https://shining-inferno-4546.firebaseio.com")
                 (.build))

          ]
      (FirebaseApp/initializeApp opts)
      (-> (FirebaseDatabase/getInstance)
          (.getReference)))))

(defn db-ref-get-in [^DatabaseReference db refs]
  "Returns a db ref to the new place in db with get-in syntax"
  (r/reduce (fn [acc e]
              (-> db
                  (.child e)))
            refs))

(defn db-ref->value [^DatabaseReference db cb]
  "Returns the value snapshot for a db ref"
  (-> db
      (.addValueEventListener (reify ValueEventListener
                                (^void onDataChange [this ^DataSnapshot snapshot]
                                  (cb snapshot))
                                (^void onCancelled [this ^DatabaseError error]
                                  (println "Error retrieving data: " error))))))

(defn data-snapshot->value [^DataSnapshot snapshot]
  (.getValue snapshot))