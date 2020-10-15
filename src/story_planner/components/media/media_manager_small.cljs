(ns story-planner.components.media.media-manager-small)



(defn Media-Manager-Small [active images on-image-select]
  [:div.MediaManagerSmall {:class (str "MediaManagerSmall--" @active)}
   [:div.MediaManagerSmall__header.standard-padding
    [:h2 "Your Images"]
    [:p {:on-click #(reset! active false)} "x"]]
   [:div.MediaManagerSmall__imageWrapper
    (for [image images]
      [:div.MediaManagerSmall__imageWrapper-image
       [:img {:src (:src image) :on-click #(do (reset! active false) (on-image-select (:src image)))}]])]])
