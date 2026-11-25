package com.mycompany.digitalconsultant.domain.model;

public enum CanalMarketing {
    INSTAGRAM("Instagram Orgânico & Reels"),
    FACEBOOK_ADS("Meta Ads (Facebook & Instagram Ads)"),
    GOOGLE_ADS("Google Ads (Search & Performance Max)"),
    TIKTOK("TikTok Ads & Orgânico"),
    LINKEDIN("LinkedIn B2B Ads & Social Selling"),
    EMAIL_MARKETING("E-mail Marketing & Automação / CRM"),
    SEO_BLOG("SEO & Conteúdo / Blog Institucional"),
    WHATSAPP("WhatsApp Marketing & Vendas Diretas"),
    YOUTUBE("YouTube Ads & Conteúdo Audiovisual"),
    OUTBOUND("Outbound Prospecting & Cold Mail");

    private final String descricao;

    CanalMarketing(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
