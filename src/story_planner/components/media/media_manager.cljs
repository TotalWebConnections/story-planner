(ns story-planner.components.media.media-manager
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.upload :refer [upload-image]]
            [story-planner.components.folder :refer [Folder-creation]]))


(defn Media-Manager [active images folders]
  (let [active-folder (atom nil)]
    (fn [active images folders]
      [:div.MediaManager {:class (str "MediaManager--" @active)}
       [:div.MediaManager__header.standard-padding
        [:h2 "Your Images"]
        [:p {:on-click #(reset! active false)} "x"]]
       [:div.MediaManager__upload
        [:input#my-file {:type "file"}]
        [:button {:on-click #(upload-image "my-file" @active-folder)}"upload image"]]
       [Folder-creation]
       (if @active-folder
         [:p (str "Current Folder: " @active-folder)])
       [:div.MediaManager__imageWrapper
        (if @active-folder
          [:i.fas.fa-level-up-alt.goBack {:on-click #(reset! active-folder nil)}]
          (for [folder folders]
            [:div.MediaManager__imageWrapper-folder {:on-click #(reset! active-folder folder)
                                                     :key (str folder "-" (rand-int 100))}
             [:i.fas.fa-folder]
             [:h3 folder]]))
        (doall (for [image images]
                 (if (or (= (:folder image) @active-folder) (and (= @active-folder nil) (= "null" (:folder image))))
                   ^{:key (str (:url image) "-" (rand-int 100))}[:div.MediaManager__imageWrapper-image
                                                                 [:img {:src (str "https://story-planner.s3.amazonaws.com/" (:url image))}]])))]])))
