/*
 * Copyright 2014 Groupon, Inc
 * Copyright 2014 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.tenant.api;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.tenant.api.TenantKV.TenantKey;
import org.killbill.billing.tenant.dao.TenantDao;
import org.killbill.billing.tenant.glue.DefaultTenantModule;

public class DefaultTenantInternalApi implements TenantInternalApi {

    private final TenantDao tenantDao;

    @Inject
    public DefaultTenantInternalApi(@Named(DefaultTenantModule.NO_CACHING_TENANT) final TenantDao tenantDao) {
        this.tenantDao = tenantDao;
    }

    @Override
    public List<String> getTenantCatalogs(final InternalTenantContext tenantContext) {
        return tenantDao.getTenantValueForKey(TenantKey.CATALOG.toString(), tenantContext);
    }

    @Override
    public String getTenantOverdueConfig(final InternalTenantContext tenantContext) {
        final List<String> values = tenantDao.getTenantValueForKey(TenantKey.OVERDUE_CONFIG.toString(), tenantContext);
        return getUniqueValue(values, "overdue config", tenantContext);
    }

    @Override
    public String getInvoiceTemplate(final Locale locale, final InternalTenantContext tenantContext) {
        final List<String> values = tenantDao.getTenantValueForKey(getKeyFromLocale(TenantKey.INVOICE_TEMPLATE_.toString(), locale), tenantContext);
        return getUniqueValue(values, "invoice template", tenantContext);
    }

    @Override
    public String getManualPayInvoiceTemplate(final Locale locale, final InternalTenantContext tenantContext) {
        final List<String> values = tenantDao.getTenantValueForKey(getKeyFromLocale(TenantKey.INVOICE_MP_TEMPLATE_.toString(), locale), tenantContext);
        return getUniqueValue(values, "manual pay invoice template", tenantContext);
    }

    @Override
    public String getInvoiceTranslation(final Locale locale, final InternalTenantContext tenantContext) {
        final List<String> values = tenantDao.getTenantValueForKey(getKeyFromLocale(TenantKey.INVOICE_TRANSLATION_.toString(), locale), tenantContext);
        return getUniqueValue(values, "invoice translation", tenantContext);
    }

    @Override
    public String getCatalogTranslation(final Locale locale, final InternalTenantContext tenantContext) {
        final List<String> values = tenantDao.getTenantValueForKey(getKeyFromLocale(TenantKey.CATALOG_TRANSLATION_.toString(), locale), tenantContext);
        return getUniqueValue(values, "catalog translation", tenantContext);
    }

    private String getUniqueValue(final List<String> values, final String msg, final InternalTenantContext tenantContext) {
        if (values.isEmpty()) {
            return null;
        }
        if (values.size() > 1) {
            throw new IllegalStateException(String.format("Unexpected number of values %d for %s and tenant %d",
                                                          values.size(), msg, tenantContext.getTenantRecordId()));
        }
        return values.get(0);
    }

    private String getKeyFromLocale(final String prefix, final Locale locale) {
        final StringBuilder tmp = new StringBuilder(prefix);
        tmp.append(locale.getLanguage())
           .append("_")
           .append(locale.getCountry());
        return tmp.toString();
    }
}