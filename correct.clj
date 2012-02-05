; Sources:
; 1. http://norvig.com/spell-correct.html
; 2. http://java.ociweb.com/mark/clojure/article.html
; 3. http://www.oracle.com/technetwork/community/bookstore/sample-clojure-485732.pdf

(use '[clojure.string :only (lower-case)])
(use '[clojure.contrib.io :only (read-lines)])

(def alphabet "abcdefghijklmnopqrstuvwxyz")

(defn train [filename]
  (let [parse-line (fn [line]
                (let [terms (re-seq #"[a-z]+" (lower-case line))]
                  (map #(vector % 1) terms)))
        combine (fn [mapped]
                  (->>
                    (apply concat mapped)
                    (group-by first)
                    (map (fn [[k v]]
                           {k (map second v)}))
                    (apply merge-with conj)))
        sum (fn [[k v]]
              {k (apply + v)})
        reduce-parsed-lines (fn [collected-values]
                               (apply merge (map sum collected-values)))]
    (->>
      (read-lines filename)
      (map parse-line)
      (combine)
      (reduce-parsed-lines))))

(def term-frequency (train "big.txt"))

(defn edits_1 [word]
  (let [splits (take (+ (count word) 1)
                    (map #(vector (.substring word 0 %1), (.substring word %1))
                         (iterate inc 0)))

        deletes (let [deletion (fn [[a b]]
                                 (when-not (empty? b)
                                   (str a (.substring b 1))))]
                  (remove #(nil? %)
                          (map deletion splits)))

        transposes (let [transposition (fn [[a b]]
                                         (when (> (count b) 1)
                                           (str a (nth b 1) (nth b 0) (.substring b 2))))]
                     (remove #(nil? %)
                             (map transposition splits)))

        replaces (let [replacement (fn [c]
                                     (let [replacement (fn [[a b]]
                                                         (when-not (empty? b)
                                                           (str a c (.substring b 1))))]
                                       (remove #(nil? %)
                                               (map replacement splits))))]
                   (apply concat (map replacement alphabet)))

        inserts (let [insertion (fn [c]
                                  (let [insertion (fn [[a b]]
                                                    (str a c b))]
                                    (map insertion splits)))]
                  (map insertion alphabet))]

    (apply concat deletes transposes replaces inserts)))

(defn known_edits_2 [word]
  (apply concat (map (fn [edit_1]
         (remove #(nil? %)
                 (map (fn [edit_2]
                        (when (contains? term-frequency edit_2)
                          edit_2))
                      (edits_1 edit_1))))
       (edits_1 word))))

(defn known [words]
  (remove #(nil? %)
          (map #(when (contains? term-frequency %)
                  %)
               words)))

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
      (apply max-key #(get term-frequency %)
             (keys (select-keys term-frequency candidates)))
      word)))

