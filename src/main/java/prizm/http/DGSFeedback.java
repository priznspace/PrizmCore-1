/******************************************************************************
 * Copyright © 2013-2016 The Nxt Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * Nxt software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package prizm.http;

import prizm.Account;
import prizm.Attachment;
import prizm.DigitalGoodsStore;
import prizm.PrizmException;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

import static prizm.http.JSONResponses.GOODS_NOT_DELIVERED;
import static prizm.http.JSONResponses.INCORRECT_PURCHASE;

public final class DGSFeedback extends CreateTransaction {

    static final DGSFeedback instance = new DGSFeedback();

    private DGSFeedback() {
        super(new APITag[] {APITag.DGS, APITag.CREATE_TRANSACTION},
                "purchase");
    }

    @Override
    protected JSONStreamAware processRequest(HttpServletRequest req) throws PrizmException {

        DigitalGoodsStore.Purchase purchase = ParameterParser.getPurchase(req);

        Account buyerAccount = ParameterParser.getSenderAccount(req);
        if (buyerAccount.getId() != purchase.getBuyerId()) {
            return INCORRECT_PURCHASE;
        }
        if (purchase.getEncryptedGoods() == null) {
            return GOODS_NOT_DELIVERED;
        }

        Account sellerAccount = Account.getAccount(purchase.getSellerId());
        Attachment attachment = new Attachment.DigitalGoodsFeedback(purchase.getId());
        return createTransaction(req, buyerAccount, sellerAccount.getId(), 0, attachment);
    }

}
