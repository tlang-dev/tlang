// SPDX-License-Identifier: Apache-2.0
// Copyright 2026 TLang Contributors

//! Built-in `TLang.Pdf.*` library.
//!
//! All mutating operations borrow the inner `PdfState` via `Rc<RefCell<>>`,
//! apply the change, and return `Ok(args[0].clone())` — the same `Rc` handle —
//! mirroring the `StringBuilder` pattern.

use std::cell::RefCell;
use std::rc::Rc;

use printpdf::{BuiltinFont, Color, Mm, PdfDocument, Rect, Rgb};
use printpdf::path::PaintMode;

use super::super::{RuntimeError, Value};
use crate::pdf_lib::PdfState;

// ── helpers ───────────────────────────────────────────────────────────────────

/// Convert a `Value::Float` or `Value::Int` to `f64`, or error.
fn as_f64(v: &Value, ctx: &str) -> Result<f64, RuntimeError> {
    match v {
        Value::Float(f) => Ok(*f),
        Value::Int(i) => Ok(*i as f64),
        _ => Err(RuntimeError(format!("{ctx}: expected Float or Int"))),
    }
}

fn as_string(v: &Value, ctx: &str) -> Result<String, RuntimeError> {
    match v {
        Value::String(s) => Ok(s.clone()),
        _ => Err(RuntimeError(format!("{ctx}: expected String"))),
    }
}

/// Map a font name string to the printpdf `BuiltinFont` variant.
fn resolve_builtin_font(name: &str) -> Option<BuiltinFont> {
    match name {
        "Helvetica" => Some(BuiltinFont::Helvetica),
        "Helvetica-Bold" => Some(BuiltinFont::HelveticaBold),
        "Helvetica-Oblique" => Some(BuiltinFont::HelveticaOblique),
        "Helvetica-BoldOblique" => Some(BuiltinFont::HelveticaBoldOblique),
        "Times-Roman" => Some(BuiltinFont::TimesRoman),
        "Times-Bold" => Some(BuiltinFont::TimesBold),
        "Times-Italic" => Some(BuiltinFont::TimesItalic),
        "Times-BoldItalic" => Some(BuiltinFont::TimesBoldItalic),
        "Courier" => Some(BuiltinFont::Courier),
        "Courier-Bold" => Some(BuiltinFont::CourierBold),
        "Courier-Oblique" => Some(BuiltinFont::CourierOblique),
        "Courier-BoldOblique" => Some(BuiltinFont::CourierBoldOblique),
        _ => None,
    }
}

/// Approximate average character width factor (units per 1000pt / font_size).
fn char_width_factor(font_name: &str) -> f64 {
    if font_name.starts_with("Courier") {
        600.0
    } else if font_name.starts_with("Times") {
        500.0
    } else {
        // Helvetica and everything else
        556.0
    }
}

/// Convert Pt to Mm (printpdf uses Mm for coordinates).
fn pt_to_mm(pt: f64) -> Mm {
    Mm(pt as f32 / 2.834_645_7)
}

// ── get or add font in state ───────────────────────────────────────────────

fn ensure_font(state: &mut PdfState, font_name: &str) -> Result<(), RuntimeError> {
    if state.fonts.contains_key(font_name) {
        return Ok(());
    }
    let builtin = resolve_builtin_font(font_name).ok_or_else(|| {
        RuntimeError(format!("TLang.Pdf: unknown font `{font_name}`; supported: Helvetica, Helvetica-Bold, Times-Roman, Times-Bold, Times-Italic, Courier, Courier-Bold"))
    })?;
    let font_ref = state
        .doc_ref()
        .add_builtin_font(builtin)
        .map_err(|e| RuntimeError(format!("TLang.Pdf: failed to add font `{font_name}`: {e}")))?;
    state.fonts.insert(font_name.to_string(), font_ref);
    Ok(())
}

// ── dispatch ──────────────────────────────────────────────────────────────────

pub(crate) fn call(target: &str, args: &[Value]) -> Result<Value, RuntimeError> {
    match target {
        "TLang.Pdf.create" => create(args),
        "TLang.Pdf.addPage" => add_page(args),
        "TLang.Pdf.setFont" => set_font(args),
        "TLang.Pdf.drawText" => draw_text(args),
        "TLang.Pdf.drawTextLine" => draw_text_line(args),
        "TLang.Pdf.moveDown" => move_down(args),
        "TLang.Pdf.setCursor" => set_cursor(args),
        "TLang.Pdf.drawRect" => draw_rect(args),
        "TLang.Pdf.fillRect" => fill_rect(args),
        "TLang.Pdf.setFillColor" => set_fill_color(args),
        "TLang.Pdf.setStrokeColor" => set_stroke_color(args),
        "TLang.Pdf.setLineWidth" => set_line_width(args),
        "TLang.Pdf.textWidth" => text_width(args),
        "TLang.Pdf.wrapText" => wrap_text(args),
        "TLang.Pdf.pageWidth" => page_width(args),
        "TLang.Pdf.pageHeight" => page_height(args),
        "TLang.Pdf.cursorY" => cursor_y(args),
        "TLang.Pdf.save" => save(args),
        _ => Err(RuntimeError(format!(
            "unknown TLang.Pdf function `{target}`"
        ))),
    }
}

// ── TLang.Pdf.create(title, widthPt, heightPt) ───────────────────────────────

fn create(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.Pdf.create expects (title: String, widthPt: Float, heightPt: Float)".to_string(),
        ));
    }
    let title = as_string(&args[0], "TLang.Pdf.create title")?;
    let width_pt = as_f64(&args[1], "TLang.Pdf.create widthPt")?;
    let height_pt = as_f64(&args[2], "TLang.Pdf.create heightPt")?;

    let (doc, page1, layer1) = PdfDocument::new(
        &title,
        pt_to_mm(width_pt),
        pt_to_mm(height_pt),
        "Layer 1",
    );

    let margin_pt = 72.0;
    let cursor_y_pt = height_pt - margin_pt;

    let mut state = PdfState {
        doc: Some(doc),
        current_page: page1,
        current_layer: layer1,
        fonts: std::collections::HashMap::new(),
        font_name: "Helvetica".to_string(),
        font_size: 12.0,
        page_width_pt: width_pt,
        page_height_pt: height_pt,
        cursor_y_pt,
        margin_pt,
        fill_r: 0.0,
        fill_g: 0.0,
        fill_b: 0.0,
        stroke_r: 0.0,
        stroke_g: 0.0,
        stroke_b: 0.0,
        line_width_pt: 1.0,
    };

    // Pre-load the default font.
    ensure_font(&mut state, "Helvetica")?;

    Ok(Value::PdfDoc(Rc::new(RefCell::new(state))))
}

// ── TLang.Pdf.addPage(doc, widthPt, heightPt) ────────────────────────────────

fn add_page(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.Pdf.addPage expects (doc: PdfDoc, widthPt: Float, heightPt: Float)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.addPage")?;
    let width_pt = as_f64(&args[1], "TLang.Pdf.addPage widthPt")?;
    let height_pt = as_f64(&args[2], "TLang.Pdf.addPage heightPt")?;

    let mut state = rc.borrow_mut();
    let (page_idx, layer_idx) =
        state
            .doc_ref()
            .add_page(pt_to_mm(width_pt), pt_to_mm(height_pt), "Layer 1");

    state.current_page = page_idx;
    state.current_layer = layer_idx;
    state.page_width_pt = width_pt;
    state.page_height_pt = height_pt;
    state.cursor_y_pt = height_pt - state.margin_pt;

    // Re-apply fill/stroke colors on new layer.
    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);
    layer.set_fill_color(Color::Rgb(Rgb::new(
        state.fill_r as f32,
        state.fill_g as f32,
        state.fill_b as f32,
        None,
    )));
    layer.set_outline_color(Color::Rgb(Rgb::new(
        state.stroke_r as f32,
        state.stroke_g as f32,
        state.stroke_b as f32,
        None,
    )));
    layer.set_outline_thickness(state.line_width_pt as f32);

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.setFont(doc, name, sizePt) ─────────────────────────────────────

fn set_font(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.Pdf.setFont expects (doc: PdfDoc, name: String, sizePt: Float)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.setFont")?;
    let font_name = as_string(&args[1], "TLang.Pdf.setFont name")?;
    let font_size = as_f64(&args[2], "TLang.Pdf.setFont sizePt")?;

    let mut state = rc.borrow_mut();
    ensure_font(&mut state, &font_name)?;
    state.font_name = font_name;
    state.font_size = font_size;

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.drawText(doc, text, xPt, yPt) ──────────────────────────────────

fn draw_text(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 4 {
        return Err(RuntimeError(
            "TLang.Pdf.drawText expects (doc: PdfDoc, text: String, xPt: Float, yPt: Float)"
                .to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.drawText")?;
    let text = as_string(&args[1], "TLang.Pdf.drawText text")?;
    let x_pt = as_f64(&args[2], "TLang.Pdf.drawText xPt")?;
    let y_pt = as_f64(&args[3], "TLang.Pdf.drawText yPt")?;

    let state = rc.borrow();
    let font_ref = state
        .fonts
        .get(&state.font_name)
        .ok_or_else(|| RuntimeError(format!("TLang.Pdf.drawText: font `{}` not loaded", state.font_name)))?
        .clone();
    let font_size = state.font_size as f32;
    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);

    layer.use_text(&text, font_size, pt_to_mm(x_pt), pt_to_mm(y_pt), &font_ref);

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.drawTextLine(doc, text, xPt) ───────────────────────────────────

fn draw_text_line(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.Pdf.drawTextLine expects (doc: PdfDoc, text: String, xPt: Float)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.drawTextLine")?;
    let text = as_string(&args[1], "TLang.Pdf.drawTextLine text")?;
    let x_pt = as_f64(&args[2], "TLang.Pdf.drawTextLine xPt")?;

    let mut state = rc.borrow_mut();
    let y_pt = state.cursor_y_pt;
    let line_height = state.font_size * 1.2;

    let font_ref = state
        .fonts
        .get(&state.font_name)
        .ok_or_else(|| {
            RuntimeError(format!(
                "TLang.Pdf.drawTextLine: font `{}` not loaded",
                state.font_name
            ))
        })?
        .clone();
    let font_size = state.font_size as f32;
    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);
    layer.use_text(&text, font_size, pt_to_mm(x_pt), pt_to_mm(y_pt), &font_ref);

    state.cursor_y_pt -= line_height;

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.moveDown(doc, pts) ─────────────────────────────────────────────

fn move_down(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Pdf.moveDown expects (doc: PdfDoc, pts: Float)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.moveDown")?;
    let pts = as_f64(&args[1], "TLang.Pdf.moveDown pts")?;

    rc.borrow_mut().cursor_y_pt -= pts;
    Ok(args[0].clone())
}

// ── TLang.Pdf.setCursor(doc, yPt) ────────────────────────────────────────────

fn set_cursor(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Pdf.setCursor expects (doc: PdfDoc, yPt: Float)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.setCursor")?;
    let y_pt = as_f64(&args[1], "TLang.Pdf.setCursor yPt")?;

    rc.borrow_mut().cursor_y_pt = y_pt;
    Ok(args[0].clone())
}

// ── TLang.Pdf.drawRect(doc, xPt, yPt, wPt, hPt) ─────────────────────────────

fn draw_rect(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 5 {
        return Err(RuntimeError(
            "TLang.Pdf.drawRect expects (doc, xPt, yPt, wPt, hPt)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.drawRect")?;
    let x_pt = as_f64(&args[1], "TLang.Pdf.drawRect xPt")?;
    let y_pt = as_f64(&args[2], "TLang.Pdf.drawRect yPt")?;
    let w_pt = as_f64(&args[3], "TLang.Pdf.drawRect wPt")?;
    let h_pt = as_f64(&args[4], "TLang.Pdf.drawRect hPt")?;

    let state = rc.borrow();
    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);
    let rect = Rect::new(
        pt_to_mm(x_pt),
        pt_to_mm(y_pt),
        pt_to_mm(x_pt + w_pt),
        pt_to_mm(y_pt + h_pt),
    )
    .with_mode(PaintMode::Stroke);
    layer.add_rect(rect);

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.fillRect(doc, xPt, yPt, wPt, hPt) ─────────────────────────────

fn fill_rect(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 5 {
        return Err(RuntimeError(
            "TLang.Pdf.fillRect expects (doc, xPt, yPt, wPt, hPt)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.fillRect")?;
    let x_pt = as_f64(&args[1], "TLang.Pdf.fillRect xPt")?;
    let y_pt = as_f64(&args[2], "TLang.Pdf.fillRect yPt")?;
    let w_pt = as_f64(&args[3], "TLang.Pdf.fillRect wPt")?;
    let h_pt = as_f64(&args[4], "TLang.Pdf.fillRect hPt")?;

    let state = rc.borrow();
    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);
    let rect = Rect::new(
        pt_to_mm(x_pt),
        pt_to_mm(y_pt),
        pt_to_mm(x_pt + w_pt),
        pt_to_mm(y_pt + h_pt),
    )
    .with_mode(PaintMode::Fill);
    layer.add_rect(rect);

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.setFillColor(doc, r, g, b) ─────────────────────────────────────

fn set_fill_color(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 4 {
        return Err(RuntimeError(
            "TLang.Pdf.setFillColor expects (doc, r, g, b) with 0.0–1.0 floats".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.setFillColor")?;
    let r = as_f64(&args[1], "TLang.Pdf.setFillColor r")?;
    let g = as_f64(&args[2], "TLang.Pdf.setFillColor g")?;
    let b = as_f64(&args[3], "TLang.Pdf.setFillColor b")?;

    let mut state = rc.borrow_mut();
    state.fill_r = r;
    state.fill_g = g;
    state.fill_b = b;

    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);
    layer.set_fill_color(Color::Rgb(Rgb::new(r as f32, g as f32, b as f32, None)));

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.setStrokeColor(doc, r, g, b) ───────────────────────────────────

fn set_stroke_color(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 4 {
        return Err(RuntimeError(
            "TLang.Pdf.setStrokeColor expects (doc, r, g, b) with 0.0–1.0 floats".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.setStrokeColor")?;
    let r = as_f64(&args[1], "TLang.Pdf.setStrokeColor r")?;
    let g = as_f64(&args[2], "TLang.Pdf.setStrokeColor g")?;
    let b = as_f64(&args[3], "TLang.Pdf.setStrokeColor b")?;

    let mut state = rc.borrow_mut();
    state.stroke_r = r;
    state.stroke_g = g;
    state.stroke_b = b;

    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);
    layer.set_outline_color(Color::Rgb(Rgb::new(r as f32, g as f32, b as f32, None)));

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.setLineWidth(doc, pts) ─────────────────────────────────────────

fn set_line_width(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Pdf.setLineWidth expects (doc: PdfDoc, pts: Float)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.setLineWidth")?;
    let pts = as_f64(&args[1], "TLang.Pdf.setLineWidth pts")?;

    let mut state = rc.borrow_mut();
    state.line_width_pt = pts;

    let layer = state.doc_ref().get_page(state.current_page).get_layer(state.current_layer);
    layer.set_outline_thickness(pts as f32);

    drop(state);
    Ok(args[0].clone())
}

// ── TLang.Pdf.textWidth(doc, text) ───────────────────────────────────────────

fn text_width(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Pdf.textWidth expects (doc: PdfDoc, text: String)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.textWidth")?;
    let text = as_string(&args[1], "TLang.Pdf.textWidth text")?;

    let state = rc.borrow();
    let factor = char_width_factor(&state.font_name);
    let width = text.chars().count() as f64 * factor / 1000.0 * state.font_size;

    Ok(Value::Float(width))
}

// ── TLang.Pdf.wrapText(doc, text, maxWidthPt) ────────────────────────────────

fn wrap_text(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 3 {
        return Err(RuntimeError(
            "TLang.Pdf.wrapText expects (doc: PdfDoc, text: String, maxWidthPt: Float)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.wrapText")?;
    let text = as_string(&args[1], "TLang.Pdf.wrapText text")?;
    let max_width = as_f64(&args[2], "TLang.Pdf.wrapText maxWidthPt")?;

    let state = rc.borrow();
    let factor = char_width_factor(&state.font_name);
    let font_size = state.font_size;

    let char_width = |s: &str| s.chars().count() as f64 * factor / 1000.0 * font_size;

    let words: Vec<&str> = text.split_whitespace().collect();
    let mut lines: Vec<Value> = Vec::new();
    let mut current_line = String::new();

    for word in words {
        if current_line.is_empty() {
            // First word on a new line — always include it even if it's too long.
            current_line = word.to_string();
        } else {
            let candidate = format!("{current_line} {word}");
            if char_width(&candidate) <= max_width {
                current_line = candidate;
            } else {
                lines.push(Value::String(current_line));
                current_line = word.to_string();
            }
        }
    }
    if !current_line.is_empty() {
        lines.push(Value::String(current_line));
    }

    Ok(Value::List(lines))
}

// ── TLang.Pdf.pageWidth(doc) ─────────────────────────────────────────────────

fn page_width(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Pdf.pageWidth expects (doc: PdfDoc)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.pageWidth")?;
    let w = rc.borrow().page_width_pt;
    Ok(Value::Float(w))
}

// ── TLang.Pdf.pageHeight(doc) ────────────────────────────────────────────────

fn page_height(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Pdf.pageHeight expects (doc: PdfDoc)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.pageHeight")?;
    let h = rc.borrow().page_height_pt;
    Ok(Value::Float(h))
}

// ── TLang.Pdf.cursorY(doc) ───────────────────────────────────────────────────

fn cursor_y(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 1 {
        return Err(RuntimeError(
            "TLang.Pdf.cursorY expects (doc: PdfDoc)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.cursorY")?;
    let y = rc.borrow().cursor_y_pt;
    Ok(Value::Float(y))
}

// ── TLang.Pdf.save(doc, path) ────────────────────────────────────────────────
//
// `PdfDocumentReference::save()` consumes `self` (takes ownership).  We store
// the document in `Option<PdfDocumentReference>` inside `PdfState` so that
// `save` can take it out via `RefCell::borrow_mut()` without needing sole
// ownership of the outer `Rc`.  After `save`, the `doc` field is `None`.

fn save(args: &[Value]) -> Result<Value, RuntimeError> {
    if args.len() != 2 {
        return Err(RuntimeError(
            "TLang.Pdf.save expects (doc: PdfDoc, path: String)".to_string(),
        ));
    }
    let rc = extract_pdf_doc(&args[0], "TLang.Pdf.save")?;
    let path = as_string(&args[1], "TLang.Pdf.save path")?;

    // Take the PdfDocumentReference out of the Option.
    let pdf_doc = rc
        .borrow_mut()
        .take_doc()
        .ok_or_else(|| RuntimeError("TLang.Pdf.save: document has already been saved".to_string()))?;

    // Serialize to bytes first (safe because take_doc() returned owned value).
    let bytes = pdf_doc
        .save_to_bytes()
        .map_err(|e| RuntimeError(format!("TLang.Pdf.save: serialization failed: {e}")))?;

    std::fs::write(&path, &bytes)
        .map_err(|e| RuntimeError(format!("TLang.Pdf.save: cannot write file `{path}`: {e}")))?;

    Ok(Value::String(path))
}

// ── helper: extract Rc<RefCell<PdfState>> ────────────────────────────────────

fn extract_pdf_doc(
    v: &Value,
    ctx: &str,
) -> Result<Rc<RefCell<PdfState>>, RuntimeError> {
    match v {
        Value::PdfDoc(rc) => Ok(rc.clone()),
        _ => Err(RuntimeError(format!(
            "{ctx}: first argument must be a PdfDoc (created with TLang.Pdf.create)"
        ))),
    }
}
