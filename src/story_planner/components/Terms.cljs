(ns story-planner.components.Terms)


(defn Terms [active on-close]
  [:div.Terms {:class (if active "active")}
   [:div.Terms__inner
    [:div.Terms__inner--close
     [:p.closeButton {:on-click #(on-close)} "Close X"]]
    [:h1 "Terms Of Service"]
    [:p "Last update: April 21, 2021"]
    [:p "Narrative Planner is a web software operated by Total Web Connections LLC (Also referred to as we). Hereafter, any mention of Narrative Planner is assumed to reference Total Web Connections LLC, the legal owners of the Narrative Planner software. By using the Narrative Planner website or web software (the \"service\") you are agreeing to be bound by the following terms and conditions. By agreeing to these terms you are entering into a legal contract between yourself and Total Web Connections LLC. If you disagree with any of the terms below please do not use Narrative Planner. These terms govern the relationship between you and Total Web Connections LLC, and grant rights and responsibilities to both parties."]
    [:p "We reserve the right to change, modify or update the terms of service any anytime with or without notice. Your continued use of the service constitutes your acceptance of these changes. Please check back regularly as this web page will contain any such updates."]

    [:h2 "1. General Terms and Conditions"]
    [:p "Below is a list of your responsibilities while using the service as well as rules you must abide by to use the service. Failure to comply with any of the below points may result in loss of use of the service or having your account terminated."]
    [:ul
     [:li "You must be 18 years or older to use the service"]
     [:li "You must provide valid and legally correct information when signing up for the service"]
     [:li "You are responsible for the security of your account, including usernames and passwords. Total Web Connections is not liable for any losses or damages that may occur from failure to do so"]
     [:li "You may not use the service for any illegal purposes"]
     [:li "Your use of the service must not violate any laws in your jurisdiction"]
     [:li "While Total Web Connections has ensured best practices are followed in regards to data encryption, you understand that unencrypted data transfers may occur and are out of our control"]
     [:li "We reserve the right to cancel the service for any account at any time for any reason. If your service is cancelled you are not entitled to a refund but we will cancel any future payments"]
     [:li "You agree to not copy, duplicate, or sell any part of the service or website"]
     [:li "Total Web Connections may use third parties for certain tasks such as hosting, data storage, and marketing. We have no control over these services and you should refer to their individual policies to learn how they store and manage your data."]
     [:li "The service is provided \"As-Is\", and \"As-Available\". We do not guarantee the service will be 100% available or meet your specific requirements"]
     [:li "The failure to enforce any right or provision of these terms does not constitute a waiver of that right or provision."]]

    [:h2 "2. Pricing and Payments"]
    [:p "You must provide a valid credit card to use the service after the trial period. Total Web Connections may close your account at any time should a payment fail to post by its due date. Pricing for the service may change at any time. Any changes to pricing will be sent to the email on file with the account, and such email will be sent no less than (30) days before the new pricing structure is to take effect."]

    [:h2 "3. Data Integrity"]
    [:p "While we do take securing and maintaining your data a priority we cannot guarantee that it will always be available. You agree to hold us harmless for any incidents that cause a loss of data."]

    [:h2 "4. Account Cancellations and Refunds"]
    [:p "You may cancel your account at any time by contacting us at support@narrative planner.com or by using the account cancellation feature in the service. Cancelling your account may cause a loss of ALL data stored with us and we are not responsible for any issues, damages, or otherwise that may arise from such loss."]
    [:p "No refunds are given for cancellations, by canceling your account you agree to forfeit and monies paid to us, and grant us the right to immediately revoke your access to the service."]

    [:h2 "5. User Generated Content"]
    [:p "Users of the service have the ability to upload user-generated content such as images and text. You agree that you have the rights to content uploaded or added to the platform and that doing so will not infringe on any other parties rights nor violate any laws."]
    [:p "Total Web Connections may, but is not obligated to, review content uploaded to the service. Total Web Connections reserves the right to remove any content for any or no reason with or without notice. Users found uploading content that violates copyright or any other laws are subject to account termination and potential legal action."]
    [:p "You also agree that you are solely responsible for any content added to the service. YOU WILL INDEMNIFY AND HOLD Total Web Connections HARMLESS FROM AND AGAINST ALL DAMAGES, LOSSES, AND EXPENSES OF ANY KIND (INCLUDING REASONABLE ATTORNEY FEES AND COSTS) ARISING OUT OF SUCH CLAIM."]

    [:h2 "6. Age requirements"]
    [:p "The service is intended for use by adults and does not knowingly collect information from children under 13 years of age. If you are the parent or legal guardian of a child under 13 who you believe has submitted personal information to us please contact us at support@narrativeplanner.com"]
    [:p "You must be at least 18 years of age to subscribe to the service. Membership in the service is void where prohibited. By using the service, you represent and warrant that you have the right, authority, and capacity to enter into this agreement and agree to abide by all the terms and conditions of it."]

    [:h2 "7. Limitation of Liability"]
    [:p "Except in jurisdictions where such provisions are restricted, in no event will Total Web Connections be liable to you or any third person for any direct, indirect, consequential, exemplary, incidental, special or punitive damages, including but not limited to, damages for loss of profits, goodwill, use, data or other intangible losses (even if Total Web Connections has been advised of the possibility of such damages), resulting from: (i) the use or the inability to use the Service]]]); (ii) the cost of procurement of substitute goods and services resulting from your inability to access or obtain any goods, data, information or services through or from the Service; (iii) unauthorized access to or alteration of your transmissions or data; (iv) statements or conduct of any third party on the service; or (v) any content posted on the Website or transmitted to Registrants or Members; or (vi) any inaccurate or out-of-date content produced by the tools or published in the guides or blog, or Website; or (vii) any other matter relating to the Service. Notwithstanding any provision to the contrary, Total Web Connections’s liability to you for any cause whatsoever, and regardless of the form of the action, will at all times be limited to the amount paid, if any, by you to Total Web Connections in the twelve (12) months prior to the claimed injury or damage specifically for the use the of the service."]

    [:h2 "8. Indemnity"]
    [:p "You agree to indemnify and hold Total Web Connections, its subsidiaries, affiliates, officers, agents, and other partners and employees, harmless from any loss, liability, claim, or demand, including reasonable attorneys’ fees, made by any third party due to or arising out of your use of the Service in violation of this Agreement and/or arising from a breach of this Agreement and/or any breach of your representations and warranties and/or your negligent or willful acts, and/or the violation by you of Total Web Connections or any third party’s rights, including without limitation privacy rights, other property rights, trade secret, proprietary information, trademark, copyright, or patent rights, and claims for libel slander, or unfair trade practices in connection with the use or operation of the Service. Your obligation to indemnify will survive the expiration or termination of this Agreement by either party for any reason."]

    [:h2 "9. Choice of Law"]
    [:p "If any dispute arises out of use of the service, you expressly agree that any such dispute shall be governed by the laws of the State of Illinois. You also agree and consent to exclusive jurisdiction and venue of the state and federal courts of the State of Illinois, in Dupage County, for the resolution of such a dispute."]

    [:h2 "10. Questions?"]
    [:p "If you have any questions or concerns with the above policies you can contact us at support@narrativeplanner.com"]

    [:h2 "11. Severability"]
    [:p "In case any provision or term in this contract shall be held invalid, illegal or unenforceable, the validity, legality and enforceability of the remaining provisions shall not in any way be affected or impaired thereby."]]])
