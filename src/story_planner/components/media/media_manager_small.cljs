(ns story-planner.components.media.media-manager-small
  (:require [story-planner.services.scripts.api.upload :refer [upload-image]]))

(defn Media-Manager-Small [active images folders on-image-select]
  (let [active-folder (atom nil)]
    (fn [active images folders on-image-select]
      [:div.MediaManagerSmall {:class (str "MediaManagerSmall--" @active)}
       [:div.MediaManagerSmall__header.standard-padding
        [:h2 "Your Images"]
        [:p {:on-click #(reset! active false)} "x"]]
       [:button {:on-click #(upload-image "mediaManagerSmall" nil)}"upload image"]
       [:input#mediaManagerSmall {:type "file"}]
       [:div.MediaManagerSmall__imageWrapper
        (for [folder folders]
          [:div.MediaManagerSmall__imageWrapper-folder {:on-click #(reset! active-folder folder)
                                                        :key (str folder "-" (rand-int 100))}
           [:i.fas.fa-folder]
           [:h3 folder]])
        (doall (for [image images]
                 ^{:key (str (:url image) "-" (rand-int 100))}[:div.MediaManagerSmall__imageWrapper-image
                                                               [:img {:src (str "https://story-planner.s3.amazonaws.com/" (:url image)) :on-click #(do (reset! active false) (on-image-select (:url image)))}]]))]])))
