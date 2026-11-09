package com.quiz.religiousquiz.modules.quiz.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.quiz.religiousquiz.modules.quiz.domain.AlternativeEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuestionEntity;
import com.quiz.religiousquiz.modules.quiz.domain.QuizSession;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class QuizPdfGeneratorService {

    private static final Color COR_DEEP = new Color(11, 48, 52);       // #0b3034
    private static final Color COR_GREEN = new Color(22, 140, 115);    // #168c73
    private static final Color COR_MINT_BG = new Color(237, 247, 243); // #edf7f3
    private static final Color COR_ORANGE = new Color(239, 155, 105);  // #ef9b69
    private static final Color COR_LINE = new Color(228, 235, 234);    // #e4ebea
    private static final Color COR_MUTED = new Color(105, 128, 132);   // #698084
    private static final Color COR_CORRETA = new Color(15, 110, 89);   // Verde escuro gabarito

    private static final Font FONTE_CABECALHO_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE);
    private static final Font FONTE_CABECALHO_SUB = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, new Color(180, 230, 215));
    private static final Font FONTE_BADGE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COR_DEEP);

    private static final Font FONTE_TITULO_QUIZ = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COR_DEEP);
    private static final Font FONTE_META = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, COR_MUTED);
    private static final Font FONTE_QUESTAO_NUM = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COR_GREEN);
    private static final Font FONTE_QUESTAO_TEXTO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10.5f, COR_DEEP);
    private static final Font FONTE_ALTERNATIVA = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, Color.DARK_GRAY);
    private static final Font FONTE_ALTERNATIVA_CORRETA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, COR_CORRETA);
    private static final Font FONTE_COMENTARIO = FontFactory.getFont(FontFactory.HELVETICA, 9f, new Color(40, 80, 75));
    private static final Font FONTE_RODAPE = FontFactory.getFont(FontFactory.HELVETICA, 8f, COR_MUTED);

    public byte[] gerarPdf(QuizSession quiz, boolean incluirGabarito) {
        Document document = new Document(PageSize.A4, 36, 36, 40, 44);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new RodapeNumeradoEvento());

            document.open();

            adicionarBannerInstitucional(document, incluirGabarito);
            adicionarIdentificacaoQuiz(document, quiz, incluirGabarito);

            if (!incluirGabarito) {
                adicionarCamposIdentificacaoAluno(document);
            }

            adicionarQuestoes(document, quiz.getQuestoes(), incluirGabarito);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Falha ao gerar documento PDF do quiz: " + e.getMessage(), e);
        }
    }

    private void adicionarBannerInstitucional(Document document, boolean incluirGabarito) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.2f});

        PdfPCell celulaEsquerda = new PdfPCell();
        celulaEsquerda.setBackgroundColor(COR_DEEP);
        celulaEsquerda.setPadding(12);
        celulaEsquerda.setBorder(Rectangle.NO_BORDER);

        Paragraph pMarca = new Paragraph("✧ CONVIVERE · DIÁLOGO INTER-RELIGIOSO", FONTE_CABECALHO_TITULO);
        Paragraph pDesc = new Paragraph("EdTech de Educação Religiosa, Convivência e Diálogo Intercultural", FONTE_CABECALHO_SUB);
        pDesc.setSpacingBefore(3);
        celulaEsquerda.addElement(pMarca);
        celulaEsquerda.addElement(pDesc);

        PdfPCell celulaDireita = new PdfPCell();
        celulaDireita.setBackgroundColor(COR_DEEP);
        celulaDireita.setPadding(12);
        celulaDireita.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celulaDireita.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celulaDireita.setBorder(Rectangle.NO_BORDER);

        String badgeTexto = incluirGabarito ? "GABARITO OFICIAL" : "SIMULADO ESCOLAR";
        PdfPCell badgeInterno = new PdfPCell(new Phrase(badgeTexto, FONTE_BADGE));
        badgeInterno.setBackgroundColor(incluirGabarito ? COR_ORANGE : new Color(120, 230, 191));
        badgeInterno.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgeInterno.setVerticalAlignment(Element.ALIGN_MIDDLE);
        badgeInterno.setPadding(6);
        badgeInterno.setBorder(Rectangle.NO_BORDER);

        PdfPTable tabelaBadge = new PdfPTable(1);
        tabelaBadge.setWidthPercentage(100);
        tabelaBadge.addCell(badgeInterno);
        celulaDireita.addElement(tabelaBadge);

        table.addCell(celulaEsquerda);
        table.addCell(celulaDireita);
        table.setSpacingAfter(14);

        document.add(table);
    }

    private void adicionarIdentificacaoQuiz(Document document, QuizSession quiz, boolean incluirGabarito) throws DocumentException {
        Paragraph pTitulo = new Paragraph(quiz.getTitulo(), FONTE_TITULO_QUIZ);
        pTitulo.setSpacingAfter(4);
        document.add(pTitulo);

        String tempoStr = quiz.getTempoLimiteSegundos() > 0 
                ? (quiz.getTempoLimiteSegundos() / 60) + " minutos" 
                : "Sem limite rígido";

        String metaTexto = String.format("Tradição: %s   •   Dificuldade: %s   •   Total de Questões: %d   •   Tempo Estimado: %s",
                quiz.getReligiao(), quiz.getDificuldade(), quiz.getQuestoes().size(), tempoStr);

        Paragraph pMeta = new Paragraph(metaTexto, FONTE_META);
        pMeta.setSpacingAfter(12);
        document.add(pMeta);
    }

    private void adicionarCamposIdentificacaoAluno(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 1f, 1f});

        adicionarCelulaAluno(table, "Nome do(a) Aluno(a): _________________________________________________");
        adicionarCelulaAluno(table, "Data: _____/_____/_________");
        adicionarCelulaAluno(table, "Turma: __________________");

        table.setSpacingAfter(14);
        document.add(table);
    }

    private void adicionarCelulaAluno(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONTE_META));
        cell.setPadding(6);
        cell.setBorderColor(COR_LINE);
        cell.setBackgroundColor(new Color(248, 250, 249));
        table.addCell(cell);
    }

    private void adicionarQuestoes(Document document, List<QuestionEntity> questoes, boolean incluirGabarito) throws DocumentException {
        int indice = 1;
        for (QuestionEntity questao : questoes) {
            adicionarBlocoQuestao(document, questao, indice++, incluirGabarito);
        }
    }

    private void adicionarBlocoQuestao(Document document, QuestionEntity questao, int indice, boolean incluirGabarito) throws DocumentException {
        PdfPTable tabelaQuestao = new PdfPTable(1);
        tabelaQuestao.setWidthPercentage(100);
        tabelaQuestao.setKeepTogether(true);
        tabelaQuestao.setSpacingBefore(8);
        tabelaQuestao.setSpacingAfter(10);

        PdfPCell celula = new PdfPCell();
        celula.setPadding(10);
        celula.setBorderColor(COR_LINE);
        celula.setBorderWidth(1f);
        celula.setBackgroundColor(Color.WHITE);

        // Número e Enunciado
        Paragraph pNumero = new Paragraph(String.format("QUESTÃO %02d", indice), FONTE_QUESTAO_NUM);
        pNumero.setSpacingAfter(3);
        celula.addElement(pNumero);

        Paragraph pEnunciado = new Paragraph(questao.getPergunta(), FONTE_QUESTAO_TEXTO);
        pEnunciado.setSpacingAfter(8);
        celula.addElement(pEnunciado);

        // Alternativas
        for (AlternativeEntity alt : questao.getAlternativas()) {
            boolean ehCorreta = incluirGabarito 
                    && questao.getRespostaCorretaId() != null 
                    && questao.getRespostaCorretaId().trim().equalsIgnoreCase(alt.getId().trim());

            String prefixo = incluirGabarito 
                    ? (ehCorreta ? "[ ✓ ] " : "[    ] ") 
                    : "(    ) ";

            Font fonteAlt = ehCorreta ? FONTE_ALTERNATIVA_CORRETA : FONTE_ALTERNATIVA;
            Paragraph pAlt = new Paragraph(prefixo + alt.getId() + ") " + alt.getTexto(), fonteAlt);
            pAlt.setSpacingAfter(3);
            pAlt.setIndentationLeft(8);

            if (ehCorreta) {
                pAlt.add(new Chunk("  ◄ GABARITO OFICIAL", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f, COR_CORRETA)));
            }
            celula.addElement(pAlt);
        }

        // Justificativa pedagógica se incluir gabarito
        if (incluirGabarito && questao.getExplicacao() != null && !questao.getExplicacao().isBlank()) {
            PdfPTable tabelaExp = new PdfPTable(1);
            tabelaExp.setWidthPercentage(100);
            tabelaExp.setSpacingBefore(6);

            PdfPCell celulaExp = new PdfPCell();
            celulaExp.setBackgroundColor(COR_MINT_BG);
            celulaExp.setBorderColor(COR_GREEN);
            celulaExp.setBorderWidthLeft(3f);
            celulaExp.setBorderWidthTop(0);
            celulaExp.setBorderWidthRight(0);
            celulaExp.setBorderWidthBottom(0);
            celulaExp.setPadding(8);

            Paragraph pExp = new Paragraph("💡 Comentário Pedagógico: " + questao.getExplicacao(), FONTE_COMENTARIO);
            celulaExp.addElement(pExp);

            if (questao.getReferencia() != null && !questao.getReferencia().isBlank()) {
                Paragraph pRef = new Paragraph("📖 Fonte/Referência: " + questao.getReferencia(), 
                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8.5f, COR_MUTED));
                pRef.setSpacingBefore(2);
                celulaExp.addElement(pRef);
            }

            tabelaExp.addCell(celulaExp);
            celula.addElement(tabelaExp);
        }

        tabelaQuestao.addCell(celula);
        document.add(tabelaQuestao);
    }

    private static class RodapeNumeradoEvento extends PdfPageEventHelper {
        private final String dataGeracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable rodape = new PdfPTable(2);
            rodape.setTotalWidth(document.right() - document.left());

            PdfPCell celulaEsq = new PdfPCell(new Phrase("Convivere · Conhecer · Respeitar · Dialogar  |  Emitido em: " + dataGeracao, FONTE_RODAPE));
            celulaEsq.setBorder(Rectangle.TOP);
            celulaEsq.setBorderColor(COR_LINE);
            celulaEsq.setPaddingTop(5);

            PdfPCell celulaDir = new PdfPCell(new Phrase(String.format("Página %d", writer.getPageNumber()), FONTE_RODAPE));
            celulaDir.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celulaDir.setBorder(Rectangle.TOP);
            celulaDir.setBorderColor(COR_LINE);
            celulaDir.setPaddingTop(5);

            rodape.addCell(celulaEsq);
            rodape.addCell(celulaDir);
            rodape.writeSelectedRows(0, -1, document.left(), document.bottom() - 10, writer.getDirectContent());
        }
    }
}

