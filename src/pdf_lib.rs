// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

use std::collections::HashMap;
use printpdf::{IndirectFontRef, PdfDocumentReference, PdfLayerIndex, PdfPageIndex};

pub struct PdfState {
    /// The PDF document.  Wrapped in `Option` so `save` can take ownership
    /// out of the `RefCell` without needing sole Rc ownership.
    pub doc: Option<PdfDocumentReference>,
    pub current_page: PdfPageIndex,
    pub current_layer: PdfLayerIndex,
    pub fonts: HashMap<String, IndirectFontRef>,
    pub font_name: String,
    pub font_size: f64,
    pub page_width_pt: f64,
    pub page_height_pt: f64,
    pub cursor_y_pt: f64,
    pub margin_pt: f64,
    pub fill_r: f64,
    pub fill_g: f64,
    pub fill_b: f64,
    pub stroke_r: f64,
    pub stroke_g: f64,
    pub stroke_b: f64,
    pub line_width_pt: f64,
}

impl PdfState {
    /// Take the PDF document out for saving.  After this call the state
    /// should not be used (the `doc` field is `None`).
    pub fn take_doc(&mut self) -> Option<PdfDocumentReference> {
        self.doc.take()
    }

    /// Borrow the PDF document reference.
    pub fn doc_ref(&self) -> &PdfDocumentReference {
        self.doc.as_ref().expect("PdfDoc has already been saved and cannot be used further")
    }
}

// PdfDocumentReference doesn't implement Debug; provide a manual impl.
impl std::fmt::Debug for PdfState {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.debug_struct("PdfState")
            .field("font_name", &self.font_name)
            .field("font_size", &self.font_size)
            .field("page_width_pt", &self.page_width_pt)
            .field("page_height_pt", &self.page_height_pt)
            .field("cursor_y_pt", &self.cursor_y_pt)
            .finish_non_exhaustive()
    }
}
