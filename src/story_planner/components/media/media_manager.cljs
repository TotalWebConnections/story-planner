(ns story-planner.components.media.media-manager)


(defn Media-Manager [active images]
  [:div.MediaManager {:class (str "MediaManager--" @active)}
   [:div.MediaManager__header.standard-padding
    [:h2 "Your Images"]
    [:p {:on-click #(reset! active false)} "x"]]
   [:div.MediaManager__imageWrapper
    (for [image images]
      [:div.MediaManager__imageWrapper-image
       [:img {:src (:src image)}]])]])
