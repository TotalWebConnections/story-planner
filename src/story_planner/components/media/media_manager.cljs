(ns story-planner.components.media.media-manager
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.core.async :refer [take!]]
            [story-planner.services.scripts.api.upload :refer [upload-image delete-image]]
            [story-planner.components.media.folder :refer [Folder-creation]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.project.Confirmation :refer [Confirmation]]
            [story-planner.components.Loader :refer [Loader]]))

(defn handle-delete-image [showDeleteImage]
  (delete-image (:url @showDeleteImage))
  (reset! showDeleteImage {:active nil}))

(defn on-img-response [isUploading? val]
  (reset! isUploading? false))

(defn handle-upload-image [active-folder isUploading?]
  (reset! isUploading? true)
  (take! (upload-image "my-file" active-folder) (partial on-img-response isUploading?)))

(defn Media-Manager [active images folders on-image-click & [need-dispatch]]
  (let [active-folder (atom nil)
        showDeleteImage (atom {:active nil})
        isUploading? (atom false)]
    (fn [active images folders]
      [:div.MediaManager {:class (str "MediaManager--" @active)}
       [Confirmation (:active @showDeleteImage) "Really Delete This Image?" #(handle-delete-image showDeleteImage) #(reset! showDeleteImage {:active nil})]
       (if @isUploading?
         [Loader])
       [:div.MediaManager__header.standard-padding
        [:h2 "Your Images"]
        [:p.MediaManager__header__close {:on-click #(if need-dispatch (handle-state-change {:type "app-show-media-manager" :value false}) (reset! active false))} "x"]]
       [:div.MediaManager__upload
        [:input#my-file {:type "file"}]
        [:button {:on-click #(handle-upload-image @active-folder isUploading?)}"Upload Image"]]
       [Folder-creation]
       (if @active-folder
         [:div.MediaManager__currentFolder
          [:i.fas.fa-level-up-alt.goBack {:on-click #(reset! active-folder nil)}]
          [:p (str "Current Folder: " @active-folder)]])
       [:div.MediaManager__imageWrapper
        (if (not @active-folder)
          (for [folder folders]
            [:div.MediaManager__imageWrapper-folder {:on-click #(reset! active-folder folder)
                                                     :key (str folder "-" (rand-int 100))}
             [:i.fas.fa-folder]
             [:h3 folder]]))
        (doall (for [image images]
                 (if (or (= (:folder image) @active-folder) (and (= @active-folder nil) (= "null" (:folder image))))
                   ^{:key (str (:url image) "-" (rand-int 100))}[:div.MediaManager__imageWrapper-image {:on-click #(on-image-click (:url image))}
                                                                 [:div.MediaManager__imageWrapper-image-close {:on-click #(reset! showDeleteImage {:url (:url image) :active "active"})}
                                                                  [:p "X"]]
                                                                 [:img {:src (str "https://story-planner.s3.amazonaws.com/" (:url image))}]])))]])))
