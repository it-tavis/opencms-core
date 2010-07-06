/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/ade/galleries/preview/image/client/Attic/CmsImagePreviewHandler.java,v $
 * Date   : $Date: 2010/07/05 14:48:07 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.galleries.preview.image.client;

import org.opencms.ade.galleries.client.preview.A_CmsPreviewHandler;
import org.opencms.ade.galleries.shared.CmsImageInfoBean;

/**
 * Image preview dialog controller handler.<p>
 * 
 * Delegates the actions of the preview controller to the preview dialog.
 * 
 * @author Polina Smagina
 * 
 * @version $Revision: 1.2 $ 
 * 
 * @since 8.0.0
 */
public class CmsImagePreviewHandler extends A_CmsPreviewHandler<CmsImageInfoBean> {

    /**
     * Constructor.<p>
     * 
     * @param previewDialog the reference to the preview dialog 
     */
    public CmsImagePreviewHandler(CmsImagePreviewDialog previewDialog) {

        super(previewDialog);
        previewDialog.init(this);
    }

    /**
     * Initializes the preview handler.<p>
     * 
     * @param controller the preview controller
     */
    public void init(CmsImagePreviewController controller) {

        m_controller = controller;
    }
}