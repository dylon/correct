; Sources:
; 1. http://norvig.com/spell-correct.html
; 2. http://java.ociweb.com/mark/clojure/article.html
; 3. http://www.oracle.com/technetwork/community/bookstore/sample-clojure-485732.pdf

(ns spelling.correction
  (:use [clojure.string :only [lower-case]]
        [clojure.contrib.io :only [read-lines]]))

(defn combine [mapped]
  (->> (apply concat mapped)
       (group-by first)
       (map (fn [[k v]]
              {k (map second v)}))
       (apply merge-with conj)))

(defn map-reduce [mapper reducer args-seq]
  (->> (map mapper args-seq)
       (combine)
       (reducer)))

(def alphabet "abcdefghijklmnopqrstuvwxyz")

(defn lex [line]
  (re-seq #"[a-z]+" (lower-case line)))

(defn tokenize [lexeme]
  (vector lexeme 1))

(defn parse-line [line]
  (map tokenize (lex line)))

(defn sum [[k v]]
  {k (apply + v)})

(defn reduce-parsed-lines [collected-values]
  (apply merge (map sum collected-values)))

(defn term-frequency [filename]
  (map-reduce parse-line reduce-parsed-lines (read-lines filename)))

(def tf (term-frequency "big.txt"))

(defn splits [word]
  (let [split (fn [i]
                (vector (.substring word 0 i), (.substring word i)))]
    (take (+ (count word) 1) (map split (iterate inc 0)))))

(defn deletions [splits]
  (let [deletion (fn [[a b]]
                   (when-not (empty? b)
                     (str a (.substring b 1))))]
    (remove #(nil? %) (map deletion splits))))

(defn transpositions [splits]
  (let [transposition (fn [[a b]]
                        (when (> (count b) 1)
                          (str a (nth b 1) (nth b 0) (.substring b 2))))]
    (remove #(nil? %) (map transposition splits))))

(defn replacements [splits]
  (let [replacement (fn [c]
                      (let [replacement (fn [[a b]]
                                          (when-not (empty? b)
                                            (str a c (.substring b 1))))]
                        (remove #(nil? %) (map replacement splits))))]
    (apply concat (map replacement alphabet))))

(defn insertions [splits]
  (let [insertion (fn [c]
                    (let [insertion (fn
                                      [[a b]] (str a c b))]
                      (map insertion splits)))]
    (map insertion alphabet)))

(defn edits_1 [word]
  (let [splits (splits word)
        deletions (deletions splits)
        transpositions (transpositions splits)
        replacements (replacements splits)
        insertions (insertions splits)]
    (apply concat deletions transpositions replacements insertions)))

(defn known_edits_2 [word]
  (apply concat (map (fn [edit_1]
         (remove #(nil? %)
                 (map (fn [edit_2]
                        (when (contains? tf edit_2)
                          edit_2))
                      (edits_1 edit_1))))
       (edits_1 word))))

(defn known [words]
  (remove #(nil? %)
          (map #(when (contains? tf %) %) words)))

(defn correct [word]
  (let [candidates (let [known_word (known [word])
                         known_edits_1 (known (edits_1 word))
                         known_edits_2 (known_edits_2 word)]
                     (cond
                       (not (empty? known_word)) known_word
                       (not (empty? known_edits_1)) known_edits_1
                       (not (empty? known_edits_2)) known_edits_2
                       :else nil))]
    (if-not (nil? candidates)
      (apply max-key #(get tf %)
             (keys (select-keys tf candidates)))
      word)))

