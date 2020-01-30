package com.lemonlab.all_in_one.items

import android.view.View
import android.widget.ImageView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.lemonlab.all_in_one.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.ad_unified.view.*


class UnifiedAd(private val ad: UnifiedNativeAd) : Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {

        val view = viewHolder.itemView
        val adView = view as UnifiedNativeAdView

        val adIcon = view.ad_app_icon
        val adHeadline = view.ad_headline
        val adAdvertiser = view.ad_advertiser
        val adStars = view.ad_stars
        val adBody = view.ad_body
        val adMedia = view.ad_media
        val adPrice = view.ad_price
        val adStore = view.ad_store
        val adAction = view.ad_call_to_action

        if (ad.icon != null)
            adIcon.setImageDrawable(ad.icon.drawable)
        else adIcon.visibility = View.GONE

        if (ad.headline != null)
            adHeadline.text = ad.headline
        else adHeadline.visibility = View.GONE

        if (ad.advertiser != null)
            adAdvertiser.text = ad.headline
        else adAdvertiser.visibility = View.GONE

        if (ad.starRating != null)
            adStars.rating = ad.starRating.toFloat()
        else adStars.visibility = View.GONE

        if (ad.body != null)
            adBody.text = ad.body
        else adBody.visibility = View.GONE

        if (ad.mediaContent != null)
            adMedia.setMediaContent(ad.mediaContent)
        else adMedia.visibility = View.GONE

        if (ad.price != null)
            adPrice.text = ad.price
        else adPrice.visibility = View.GONE

        if (ad.store != null)
            adStore.text = ad.store
        else adStore.visibility = View.GONE

        if (ad.callToAction != null) {
            adAction.text = ad.callToAction
        } else adAction.visibility = View.GONE


        adView.mediaView = adMedia
        adView.callToActionView = adAction
        adView.storeView = adStore
        adView.priceView = adPrice
        adView.bodyView = adBody
        adView.starRatingView = adStars
        adView.advertiserView = adAdvertiser
        adView.headlineView = adHeadline
        adView.iconView = adIcon

        adMedia.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

        adView.setNativeAd(ad)


    }


    override fun getLayout() = R.layout.ad_unified
}

