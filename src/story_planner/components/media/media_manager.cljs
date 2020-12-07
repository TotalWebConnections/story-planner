(ns story-planner.components.media.media-manager
  (:require [story-planner.services.scripts.api.upload :refer [upload-image]]
            [story-planner.components.folder :refer [Folder-creation]]))


(defn Media-Manager [active images folders]
  [:div.MediaManager {:class (str "MediaManager--" @active)}
   [:div.MediaManager__header.standard-padding
    [:h2 "Your Images"]
    [:p {:on-click #(reset! active false)} "x"]]
   [:div.MediaManager__upload
    [:input#my-file {:type "file"}]
    [:button {:on-click #(upload-image "my-file")}"upload image"]]
   [Folder-creation]
   [:div.MediaManager__imageWrapper
    (for [folder folders]
      [:div.MediaManager__imageWrapper-folder
       [:i.fas.fa-folder]
       [:h3 folder]])
    (for [image images]
      [:div.MediaManager__imageWrapper-image
       [:img {:src (str "https://story-planner.s3.amazonaws.com/" (:url image))}]])]])
