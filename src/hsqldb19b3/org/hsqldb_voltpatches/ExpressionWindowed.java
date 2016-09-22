/* Copyright (c) 2001-2009, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb_voltpatches;

import java.util.List;

import org.hsqldb_voltpatches.HSQLInterface.HSQLParseException;
import org.hsqldb_voltpatches.lib.HsqlList;
import org.hsqldb_voltpatches.types.Type;


/**
 * Implementation of RANK operations
 *
 * @author Xin Jia
 */
public class ExpressionWindowed extends Expression {
    private List<Expression> m_partitionByList;
    private SortAndSlice     m_sortAndSlice;
    private boolean          m_isDistinctAggregate;

    ExpressionWindowed(int tokenT,
                       Expression aggExprs[],
                       boolean isDistinct,
                       SortAndSlice sortAndSlice,
                       List<Expression> partitionByList) {
        super(ParserBase.getExpressionType(tokenT));
        nodes = aggExprs;
        m_partitionByList = partitionByList;
        m_sortAndSlice = sortAndSlice;
        validateWindowedSyntax();
    }

    /**
     * Validate that this is a collection of values.
     */
    private void validateWindowedSyntax() {
        // Check that the aggregate is one of the supported ones, and
        // that the number of aggregate parameters is right.
        switch (opType) {
        case OpTypes.WINDOWED_RANK:
        case OpTypes.WINDOWED_DENSE_RANK:
            if (nodes.length != 0) {
                throw Error.error("Windowed Aggregate " + OpTypes.aggregateName(opType) + " expects no arguments.", "", 0);
            }
        default:
            throw Error.error("Unsupported windowed aggregate " + OpTypes.aggregateName(opType), "", 0);
        }
    }
    @Override
    public Object getValue(Session session) {
        return 0;
    }

    /**
     * Returns the data type
     */
    @Override
    Type getDataType() {
        switch (opType) {
        case OpTypes.WINDOWED_RANK:
        case OpTypes.WINDOWED_DENSE_RANK:
            return Type.SQL_BIGINT;
        default:
            throw Error.error("Unsupported aggregate type " + OpTypes.aggregateName(opType), "", 0);
        }
    }

    @Override
    public HsqlList resolveColumnReferences(RangeVariable[] rangeVarArray,
            int rangeCount, HsqlList unresolvedSet, boolean acceptsSequences) {
        HsqlList localSet = null;
        // Resolve the aggregate expression.  For the RANK-like aggregates
        // this is a no-op, because nodes is empty.
        for (Expression e : nodes) {
            localSet = e.resolveColumnReferences(RangeVariable.emptyArray, localSet);
        }
        for (Expression e : m_partitionByList) {
            localSet = e.resolveColumnReferences(
                    RangeVariable.emptyArray, localSet);
        }

        for (int i = 0; i < m_sortAndSlice.exprList.size(); i++) {
            Expression e = (Expression) m_sortAndSlice.exprList.get(i);
            assert(e instanceof ExpressionOrderBy);
            ExpressionOrderBy expr = (ExpressionOrderBy)e;
            localSet = expr.resolveColumnReferences(
                    RangeVariable.emptyArray, localSet);
        }

        if (localSet != null) {
            isCorrelated = true;
            for (int i = 0; i < localSet.size(); i++) {
                Expression e = (Expression) localSet.get(i);
                unresolvedSet = e.resolveColumnReferences(rangeVarArray,
                        unresolvedSet);
            }
            unresolvedSet = Expression.resolveColumnSet(rangeVarArray,
                    localSet, unresolvedSet);
        }

        return unresolvedSet;
    }

    @Override
    public void resolveTypes(Session session, Expression parent) {
        for (Expression expr : nodes) {
            expr.resolveTypes(session, parent);
        }
        for (Expression expr : m_partitionByList) {
            expr.resolveTypes(session, parent);
        }
        for (int i = 0; i < m_sortAndSlice.exprList.size(); i++) {
            Expression e = (Expression) m_sortAndSlice.exprList.get(i);
            e.resolveTypes(session, parent);
        }
        dataType = Type.SQL_BIGINT;
    }

    @Override
    public String getSQL() {

        StringBuffer sb = new StringBuffer();

        sb.append(OpTypes.aggregateName(opType)).append("(");
        for (Expression e : nodes) {
            sb.append(e.getSQL());
        }
        sb.append(") ")
          .append(Tokens.T_OVER + " (");
        if (m_partitionByList.size() > 0) {
            sb.append(Tokens.T_PARTITION + ' ' + Tokens.T_BY + ' ');
            for (int idx = 0; idx < m_partitionByList.size(); idx += 1) {
                Expression expr = m_partitionByList.get(idx);
                sb.append(expr.getSQL());
                if (idx < m_partitionByList.size()-1) {
                    sb.append(", ");
                } else {
                    sb.append(" ");
                }
            }
        }
        if (m_sortAndSlice.getOrderLength() > 0) {
            sb.append(Tokens.T_ORDER + ' ' + Tokens.T_BY + ' ');
            for (int idx = 0; idx < m_sortAndSlice.getOrderLength(); idx += 1) {
                Expression obExpr = (Expression) m_sortAndSlice.exprList.get(idx);
                sb.append(obExpr.getSQL())
                  .append(' ')
                  .append(m_sortAndSlice.sortDescending[idx] ? Tokens.T_DESC : Tokens.T_ASC);
                if (idx < m_sortAndSlice.getOrderLength()-1) {
                    sb.append(", ");
                } else {
                    sb.append(" ");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected String describe(Session session, int blanks) {
        return getSQL();
    }

    /**
     * Add partitionby and orderby information to the windowed aggregate XML node.
     * When we really implement windowing here, we will want to add more attributes.
     *
     * @param exp
     * @param context
     * @return
     * @throws HSQLParseException
     */
    public VoltXMLElement voltAnnotateWindowedAggregateXML(VoltXMLElement exp, SimpleColumnContext context)
            throws HSQLParseException {
        if (m_partitionByList.size() > 0) {
            VoltXMLElement pxe = new VoltXMLElement("partitionbyList");
            exp.children.add(pxe);
            for (Expression e : m_partitionByList) {
                pxe.children.add(e.voltGetXML(context, null));
            }
        }

        VoltXMLElement rxe = new VoltXMLElement("orderbyList");
        exp.children.add(rxe);

        for (int i = 0; i < m_sortAndSlice.exprList.size(); i++) {
            Expression e = (Expression) m_sortAndSlice.exprList.get(i);
            assert(e instanceof ExpressionOrderBy);
            ExpressionOrderBy expr = (ExpressionOrderBy)e;
            VoltXMLElement orderby = expr.voltGetXML(context, null);
            boolean isDecending = expr.isDescending();
            orderby.attributes.put("decending", isDecending ? "true": "false");
            rxe.children.add(orderby);
        }

        return exp;
    }

}
